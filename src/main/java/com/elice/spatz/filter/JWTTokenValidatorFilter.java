package com.elice.spatz.filter;

import com.elice.spatz.config.CustomUserDetails;
import com.elice.spatz.constants.ApplicationConstants;
import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.user.repository.UserRepository;
import com.elice.spatz.domain.user.service.TokenProvider;
import com.elice.spatz.exception.dto.UserErrorResponse;
import com.elice.spatz.exception.errorCode.UserErrorCode;
import com.elice.spatz.exception.exception.UserException;
import com.elice.spatz.exception.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final GlobalExceptionHandler globalExceptionHandler;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // access token 추출
        String accessToken = parseBearerToken(request, ApplicationConstants.JWT_HEADER);

        // 액세스 토큰이 없다면 예외 발생
        if(accessToken == null) {
            JWTTokenExceptionHandler(response, HttpStatus.UNAUTHORIZED, "AU001", "JWT Token이 존재하지 않습니다");
            return;
        }

        try {
            // 토큰 유효성을 검사
            tokenProvider.validateTokenIsExpiredOrTampered(accessToken);

            // 토큰으로부터 사용자 정보를 추출하여 인증 객체를 생성하고, 그 인증 객체를 Security Context holder 에 저장하는 과정
            setAuthenticationFromJWTToken(accessToken);

        } catch (ExpiredJwtException e) {
            // 클라이언트가 보낸 Access Token 유효기간 만료 시 Refresh Token 을 이용하여 재발급한다.
            reissueAccessToken(request, response, e);
        }
        catch (SignatureException e) {
            // 변조된 JWT 토큰을 보냈을 시 토큰이 변조되었다는 예외를 발생
            JWTTokenExceptionHandler(response, HttpStatus.BAD_REQUEST, "AU002", "JWT 토큰 변조가 감지되었습니다.");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        filterChain.doFilter(request, response);

    }



    // 클라이언트가 보낸 Refresh-Token 을 바탕으로 Access Token 을 재 발급하여 response 의 Header 에 넣는다.
    private void reissueAccessToken(HttpServletRequest request, HttpServletResponse response, ExpiredJwtException exception) {

        try {
            String refreshToken = parseBearerToken(request, "Refresh-Token");

            if (refreshToken == null) {
                throw exception;
            }

            // 기간이 만료된 액세스 토큰
            String oldAccessToken = parseBearerToken(request, ApplicationConstants.JWT_HEADER);

            // 현재 Refresh Token 이 유효한가 + 재발급 횟수가 남아있는 지 여부를 확인
            tokenProvider.validateRefreshToken(refreshToken, oldAccessToken);

            // 새로 발급된 액세스 토큰
            String newAccessToken = tokenProvider.recreateAccessToken(oldAccessToken);

            Map newAccessTokenPayload = tokenProvider.getPayloadFromJWTToken(newAccessToken);

            String username = String.valueOf(newAccessTokenPayload.get("username"));
            String authorities = String.valueOf(newAccessTokenPayload.get("authorities"));

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            response.setHeader("New-Access-Token", newAccessToken);
        } catch (Exception e) {
            request.setAttribute("exception", e);
        }
    }

    // 클라이언트 요청으로부터, Access Token 과 Refresh-Token 을 String 형태로 추출하는 메소드.
    // Access Token 과 Refresh-Token 중 어느 것을 추출할 것인지는 두 번째 인자로 전달하여 명시
    private String parseBearerToken(HttpServletRequest request, String headerName) {
        return Optional.ofNullable(request.getHeader(headerName))
                .filter(headerValue -> headerValue.substring(0, 6).equalsIgnoreCase("Bearer"))
                .map(headerValue -> headerValue.substring(7))
                .orElse(null);
    }

    // 인증(로그인)한 사용자만 접근이 가능한 리소스 (SecurityConfig -> urlsToBeAuthenticated 에도 추가하셔야 합니다.)
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/apiLogin")
                || path.equals("/users")
                || path.equals("/mails")
                || path.equals("/afterSocialLogin");
    }

    // 토큰으로부터 사용자 정보를 추출 후 인증 객체를 생성해 SecurityContextHolder 에 넣는 과정
    private void setAuthenticationFromJWTToken (String accessToken) throws JsonProcessingException {
        Map payloadFromJWTToken = tokenProvider.getPayloadFromJWTToken(accessToken);

        Long userId = Long.parseLong(String.valueOf(payloadFromJWTToken.get("userId")));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다"));

        CustomUserDetails principal = new CustomUserDetails(userId, user.getEmail(), user.getPassword(), user.getRole(), user.getNickname());

        // 여기서 첫 번째의 인자로 주입되는 principal 이, @AuthenticationPrincipal 을 통해 주입되는 사용자 정보이다.

        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
                AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole()));

        log.info(authentication.getAuthorities().toString());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 토큰에 대한 오류가 발생했을 때, 커스터마이징해서 Exception 처리 값을 클라이언트에게 알려준다.
    public void JWTTokenExceptionHandler(HttpServletResponse response, HttpStatus status, String code, String msg) {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String json = new ObjectMapper().writeValueAsString(new ExceptionResponseDto(status.toString(), code, msg));
            response.getWriter().write(json);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Data
    @AllArgsConstructor
    public static class ExceptionResponseDto {
        private String status;
        private String code;
        private String msg;
    }
}

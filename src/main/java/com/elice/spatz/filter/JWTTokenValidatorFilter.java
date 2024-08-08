package com.elice.spatz.filter;

import com.elice.spatz.config.CustomUserDetails;
import com.elice.spatz.constants.ApplicationConstants;
import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.user.repository.UserRepository;
import com.elice.spatz.domain.user.service.TokenProvider;
import com.elice.spatz.exception.errorCode.UserErrorCode;
import com.elice.spatz.exception.exception.UserException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
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
public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    private boolean isExecutedBefore = false;

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 쿠키의 Authorization Header 를 검증하여, 소셜 로그인을 통해 로그인한 사용자 여부를 판별.
        String accessTokenInCookie = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("Authorization")) {
                accessTokenInCookie = cookie.getValue();
            }
        }

        // HTTP Header 를 검증하여, 일반 로그인 사용자 여부 판별
        String accessTokenInHeader = parseBearerToken(request, ApplicationConstants.JWT_HEADER);

        // 소셜 로그인의 사용자인 경우에 JWT 토큰이 쿠키의 Authorization 키에 담겨있다.
        if (accessTokenInCookie != null) {
            try {
                // 토큰 유효성 검사
                tokenProvider.validateTokenIsExpiredOrTampered(accessTokenInCookie);

                // 토큰 페이로드 추출
                Map payloadFromJWTToken = tokenProvider.getPayloadFromJWTToken(accessTokenInCookie);

                Long userId = Long.parseLong(String.valueOf(payloadFromJWTToken.get("userId")));
                Users user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다"));

                CustomUserDetails principal = new CustomUserDetails(userId, user.getEmail(), user.getPassword(), user.getRole(), user.getNickname());


                // 인증을 마치고 인증에 성공한 유저의 정보를 Security Context 에 담는 과정
                // 여기서 첫 번째의 인자로 주입되는 principal 이, @AuthenticationPrincipal 을 통해 주입되는 사용자 정보이다.
                Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole()));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException e) {
                // 클라이언트가 보낸 Access Token 유효기간 만료 시 실행되는 블록
                reissueAccessToken(request, response, e);
            }
            catch (SignatureException e) {
                // 변조된 JWT 토큰을 보냈을 시 실행되는 블록
                throw new UserException(UserErrorCode.TAMPERED_TOKEN);
            } catch (Exception e) {
                e.printStackTrace();
            }

            filterChain.doFilter(request, response);

            // 일반 사용자의 경우 JWT 토큰이 Authorization Header 에 담겨 있다.
        } else if (accessTokenInHeader != null) {
            try {
                // 토큰 유효성 검사
                tokenProvider.validateTokenIsExpiredOrTampered(accessTokenInHeader);

                // 토큰 페이로드 추출
                Map payloadFromJWTToken = tokenProvider.getPayloadFromJWTToken(accessTokenInHeader);

                Long userId = Long.parseLong(String.valueOf(payloadFromJWTToken.get("userId")));
                Users user = userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다"));

                CustomUserDetails principal = new CustomUserDetails(userId, user.getEmail(), user.getPassword(), user.getRole(), user.getNickname());


                // 인증을 마치고 인증에 성공한 유저의 정보를 Security Context 에 담는 과정
                // 여기서 첫 번째의 인자로 주입되는 principal 이, @AuthenticationPrincipal 을 통해 주입되는 사용자 정보이다.
                Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRole()));

                SecurityContextHolder.getContext().setAuthentication(authentication);


            } catch (ExpiredJwtException e) {
                // 클라이언트가 보낸 Access Token 유효기간 만료 시 실행되는 블록
                reissueAccessToken(request, response, e);
            }
            catch (SignatureException e) {
                // 변조된 JWT 토큰을 보냈을 시 실행되는 블록
                throw new UserException(UserErrorCode.TAMPERED_TOKEN);
            } catch (Exception e) {
                e.printStackTrace();
            }

            filterChain.doFilter(request, response);
        } else {
            // 인증이 필요한 URL에 JWT 토큰 없이 접근할 경우 예외 발생
            throw new UserException(UserErrorCode.NO_JWT_TOKEN);
        }

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

    // 로그인 시에는 JWT 토큰이 없을 것이므로
    // JWT Token 검증작업이 수행되면 안 된다.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        return path.equals("/apiLogin")
                || path.equals("/users")
                || path.equals("/mails");
    }
}

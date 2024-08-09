package com.elice.spatz.config;

import com.elice.spatz.domain.user.dto.CustomOAuth2User;
import com.elice.spatz.domain.user.service.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class SocialLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String accessToken = tokenProvider.createAccessToken(customUserDetails.getId(), customUserDetails.getUsername(), customUserDetails.getRole());
        String refreshToken = tokenProvider.createRefreshToken();

        // 소셜 로그인 시에는 일반 로그인과 다르게, Authorization이라는 이름의 쿠키에만 access Token을 발급한다.
        // 백엔드에서 모든 OAuth2 로직을 처리하는 시스템 에서는 이런 방식으로 클라이언트에게 액세스 토큰을 부여해야만 한다.
        // 일반 로그인과 같이 Body 에 access token 과 refresh token 을 부여하는 방식으로는 구현하기가 매우 복잡하다.
        response.addCookie(createCookie("Authorization", accessToken, true));
        response.addCookie(createCookie("Refresh", refreshToken, true));

        response.sendRedirect("http://localhost:5173/jwtTokenTransfer");
    }

    private Cookie createCookie(String key, String value, boolean isHttpOnly) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(isHttpOnly);

        return cookie;
    }
}
package com.elice.spatz.domain.user.controller;

import com.elice.spatz.domain.user.dto.SignInResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @GetMapping("/afterSocialLogin")
    public ResponseEntity<SignInResponse> transferJWTTokenFromCookieToHeader (HttpServletRequest request, HttpServletResponse response) {


        // response 에서 쿠키를 꺼내 access Token(Authorization)과 RefreshToken을 꺼낸다
        String accessTokenInCookie = getAccessTokenInCookie(request);
        String refreshTokenFromCookie = getRefreshTokenFromCookie(request);

        if(accessTokenInCookie == null)
            return ResponseEntity.status(HttpStatus.OK).body(new SignInResponse());

        // 쿠키를 즉시 삭제
        deleteAccessTokenAndRefreshTokenFromCookie(response);


        // body 에 담아서 준다.
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SignInResponse(null, accessTokenInCookie, refreshTokenFromCookie));
    }

    private void deleteAccessTokenAndRefreshTokenFromCookie(HttpServletResponse response) {
        Cookie authorization = new Cookie("Authorization", null);
        Cookie refresh = new Cookie("Refresh", null);

        authorization.setPath("/");
        authorization.setMaxAge(0);
        refresh.setPath("/");
        refresh.setMaxAge(0);

        response.addCookie(authorization);
        response.addCookie(refresh);
    }

    private static String parseCookieByKey (HttpServletRequest request, String key) {
        String value = null;
        Cookie[] cookies = request.getCookies();

        if(cookies == null)
            return null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                value = cookie.getValue();
            }
        }
        return value;
    }

    private static String getAccessTokenInCookie(HttpServletRequest request) {
        return parseCookieByKey(request, "Authorization");
    }

    private static String getRefreshTokenFromCookie(HttpServletRequest request) {
        return parseCookieByKey(request, "Refresh");
    }
}

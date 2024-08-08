package com.elice.spatz.domain.user.dto;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final UserDtoFromSocialLogin userDtoFromSocialLogin;

    public CustomOAuth2User(UserDtoFromSocialLogin userDtoFromSocialLogin) {

        this.userDtoFromSocialLogin = userDtoFromSocialLogin;
    }

    // 여러 소셜로그인별로 제공하는 사용자 정보의 형식이 다르기 때문에, 이 attribute는 사용하지 않을 것이다
    @Override
    public Map<String, Object> getAttributes() {

        return null;
    }

    // 여러 곳에서 ROLE 을 적용하기 위해 반드시 필요.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return userDtoFromSocialLogin.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return userDtoFromSocialLogin.getName();
    }

    public String getEmail() {
        return userDtoFromSocialLogin.getEmail();
    }

    public String getRole() {
        return userDtoFromSocialLogin.getRole();
    }

    public String getUsername() {

        return userDtoFromSocialLogin.getUsername();
    }

    public Long getId() {
        return userDtoFromSocialLogin.getId();
    }
}

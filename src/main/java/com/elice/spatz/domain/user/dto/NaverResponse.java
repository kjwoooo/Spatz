package com.elice.spatz.domain.user.dto;

import java.util.Map;

// 네이버 소셜 로그인 완료 시 (인증 서버로부터 얻어온 액세스 토큰을, 유저 정보가 저장된 리소스 서버에다가 보낸 후에) 얻어오는 유저 정보의 형식
public class NaverResponse implements OAuth2Response{

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response");
    }


    @Override
    public String getProvider() {

        return "naver";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getName() {

        return attribute.get("name").toString();
    }
}

package com.elice.spatz.domain.user.service;

import com.elice.spatz.domain.user.dto.*;
import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// 인증 서버로부터 얻어온 액세스 토큰을, 유저 정보가 저장된 리소스 서버에 제출하면 해당 유저의 정보가 응답되는데,
// 해당 유저 정보를 받아오기 위한 서비스 클래스를 정의하여야 한다.
@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    // 리소스 서버로부터 유저 정보를 얻어오기
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        // 어느 어느 종류의 소셜 로그인인지 확인하기 위한 식별자
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        // 만약 네이버 소셜 로그인 정보라면
        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            // 만약 구글 로그인정보라면
        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        // 각 SNS 에서 로그인한 사용자들의 이름이 겹칠 수 있기 때문에, 같은 이름이라도 SNS 가 다르면 겹치지 않게 하기 위한 부분이다.
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        String email = oAuth2Response.getEmail();

        userRepository.findByEmail(oAuth2Response.getEmail()).ifPresentOrElse(
                // 만약 이미 존재하는 사용자라면 사용자의 닉네임 정보 변경
                (user) -> {
                    user.loginBySocial(username);
                },
                // 존재하지 않는 사용자라면 우리 회원 데이터베이스에 저장하여 관리할 수 있도록 하기
                () -> {
                    Users user = new Users(
                            email,
                            null,
                            username,
                            LocalDateTime.now(),
                            false,
                            true,
                            "ROLE_USER",
                            true
                    );
                    Users savedUser = userRepository.save(user);
                }
        );

        Long id = userRepository.findByEmail(email).get().getId();


        UserDtoFromSocialLogin userDtoFromSocialLogin = new UserDtoFromSocialLogin();
        userDtoFromSocialLogin.setId(id);
        userDtoFromSocialLogin.setUsername(username);
        userDtoFromSocialLogin.setName(oAuth2Response.getName());
        userDtoFromSocialLogin.setRole("ROLE_USER");
        userDtoFromSocialLogin.setEmail((oAuth2Response.getEmail()));

        return new CustomOAuth2User(userDtoFromSocialLogin);

    }

}

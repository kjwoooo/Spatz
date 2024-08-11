package com.elice.spatz.domain.user.service;

import com.elice.spatz.domain.user.dto.*;
import com.elice.spatz.domain.user.entity.UserRefreshToken;
import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.user.repository.UserRefreshTokenRepository;
import com.elice.spatz.domain.user.repository.UserRepository;
import com.elice.spatz.exception.errorCode.UserErrorCode;
import com.elice.spatz.exception.exception.UserException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final Environment env;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignInResponse signIn(SignInRequest signInRequest) {

        String accessJwtToken = "";
        String refreshJwtToken = "";

        // 사용자가 입력한 아이디와 비밀번호를 통해서 인증작업 진행
        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.unauthenticated(signInRequest.getUsername(), signInRequest.getPassword());

        // 사용자가 입력한 데이터로 수행한 인증 결과를 반환한다.
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);

        // 인증이 성공적으로 수행되었다면 다음 블록을 수행
        if(null != authenticationResponse && authenticationResponse.isAuthenticated()) {

            Users user = userRepository.findByEmail(authenticationResponse.getName())
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

            // JWT Access Token 생성
            accessJwtToken = tokenProvider.createAccessToken(
                    user.getId(), user.getNickname(), user.getEmail(), user.getRole());
//                    user.getId(),
//                    authenticationResponse.getName(),
//                    authenticationResponse.getAuthorities().stream().map(
//                            GrantedAuthority::getAuthority).collect(Collectors.joining(",")));

            // JWT Refresh Token 생성
            String refreshToken = tokenProvider.createRefreshToken();
            refreshJwtToken = refreshToken;

            // 이미 DB에 저장중인 Refresh Token 이 있다면 갱신하고, 없다면 DB에 추가하기
            userRefreshTokenRepository.findByUser(user).ifPresentOrElse(
                    it -> it.updateRefreshToken(refreshToken),
                    () -> userRefreshTokenRepository.save(new UserRefreshToken(user, refreshToken))
            );
        }

        return new SignInResponse(signInRequest.getUsername(), accessJwtToken, refreshJwtToken);	// 생성자에 토큰 추가
    }

    // 회원 가입 기능
    public UserRegisterResultDto register(UserRegisterDto userRegisterDto) {

        // 이미 사용 중인 이메일일 경우에는 예외 반환
        userRepository.findByEmail(userRegisterDto.getEmail()).ifPresent(user -> {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_IN_USE);
        });

        // 이미 사용 중인 닉네임일 경우에는 예외 반환
        userRepository.findByNickname(userRegisterDto.getNickname()).ifPresent(user -> {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_IN_USE);
        });


        Users newUser = new Users(
                userRegisterDto.getEmail(),
                userRegisterDto.getPassword(),
                userRegisterDto.getNickname(),
                null,
                userRegisterDto.isMarketingAgreed(),
                false,
                "ROLE_USER",
                true
        );

        userRepository.save(newUser);

        return new UserRegisterResultDto(true, null);
    }

    // 비밀 번호 변경 메소드
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest passwordChangeRequest) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        // 비밀 번호 변경
        String newEncodedPassword = passwordEncoder.encode(passwordChangeRequest.getPassword());
        user.changePassword(newEncodedPassword);

    }

    public Users findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    public Users findByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    @Transactional
    public void changeRandomPasswordByEmail(String email, String randomPassword) {
        Users user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        String encodedRandomPassword = passwordEncoder.encode(randomPassword);
        user.changePassword(encodedRandomPassword);
    }

    public void checkPasswordByUserId(Long userId, String password) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(UserErrorCode.INVALID_PASSWORD);
        }
    }

    @Transactional
    public void changeEmail(Long userId, String email) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        // 이메일 변경
        user.changeEmail(email);
    }

    @Transactional
    public void changeNickname(Long userId, String nickname) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        // 닉네임 변경
        user.changeNickname(nickname);
    }

    @Transactional
    public void updateActivation(Long userId, boolean activationStatus) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        // 계정 활성화 / 비활성화
        user.changeActivationStatus(activationStatus);
    }
}

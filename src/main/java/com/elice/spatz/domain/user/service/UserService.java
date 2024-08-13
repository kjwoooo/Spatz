package com.elice.spatz.domain.user.service;

import com.elice.spatz.domain.user.dto.*;
import com.elice.spatz.domain.user.entity.UserRefreshToken;
import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.user.entity.UsersProfileImage;
import com.elice.spatz.domain.user.repository.UserRefreshTokenRepository;
import com.elice.spatz.domain.user.repository.UserRepository;
import com.elice.spatz.domain.user.repository.UsersProfileImageRepository;
import com.elice.spatz.exception.errorCode.UserErrorCode;
import com.elice.spatz.exception.exception.UserException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UsersProfileImageRepository usersProfileImageRepository;

    @Value("${spring.cloud.gcp.storage.credentials.location}")
    private String keyFileName;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Transactional
    public SignInResponse signIn(SignInRequest signInRequest) {

        String accessJwtToken = "";
        String refreshJwtToken = "";
        String profileImage = "nil";

        // 사용자가 입력한 아이디와 비밀번호를 통해서 인증작업 진행
        UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken.unauthenticated(signInRequest.getUsername(), signInRequest.getPassword());

        // 사용자가 입력한 데이터로 수행한 인증 결과를 반환한다.
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);

        Users user = userRepository.findByEmail(authenticationResponse.getName())
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // 인증이 성공적으로 수행되었다면 다음 블록을 수행
        if(authenticationResponse.isAuthenticated()) {
            // JWT Access Token 생성
            accessJwtToken = tokenProvider.createAccessToken(
                    user.getId(), user.getNickname(), user.getEmail(), user.getRole());

            // JWT Refresh Token 생성
            String refreshToken = tokenProvider.createRefreshToken();
            refreshJwtToken = refreshToken;

            // 이미 DB에 저장중인 Refresh Token 이 있다면 갱신하고, 없다면 DB에 추가하기
            userRefreshTokenRepository.findByUser(user).ifPresentOrElse(
                    it -> it.updateRefreshToken(refreshToken),
                    () -> userRefreshTokenRepository.save(new UserRefreshToken(user, refreshToken))
            );
        }

        // 만약 해당 유저의 프로필 이미지가 존재한다면
        if(user.getUsersProfileImage() != null)
            profileImage = user.getUsersProfileImage().getImageUrl();

        return new SignInResponse(signInRequest.getUsername(), profileImage, accessJwtToken, refreshJwtToken);	// 생성자에 토큰 추가
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

    @Transactional
    public void deleteUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        // 계정 삭제
        userRepository.delete(user);
    }

    @Transactional
    public String updateUserProfileImage(Long userId, MultipartFile multipartFile) throws IOException {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        InputStream keyFile = ResourceUtils.getURL(keyFileName).openStream();

        String uuid = UUID.randomUUID().toString();
        String ext = multipartFile.getContentType();

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(keyFile))
                .build()
                .getService();

        String imgUrl = "https://storage.googleapis.com/" + bucketName + "/" + uuid;

        if (multipartFile.isEmpty()) {
            imgUrl = null;
        } else {
            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, uuid)
                    .setContentType(ext).build();

            Blob blob = storage.create(blobInfo, multipartFile.getInputStream());
        }


        // 만약 기존에 등록된 유저 이미지가 없다면
        if (user.getUsersProfileImage() == null) {
            UsersProfileImage usersProfileImage = new UsersProfileImage(user, imgUrl);
            usersProfileImageRepository.save(usersProfileImage);
            user.changeProfileImage(usersProfileImage);
        } else {
            user.getUsersProfileImage().changeImageUrl(imgUrl);
        }

        return imgUrl;
    }
}

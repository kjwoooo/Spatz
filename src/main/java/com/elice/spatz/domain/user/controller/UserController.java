package com.elice.spatz.domain.user.controller;

import com.amazonaws.Response;
import com.elice.spatz.config.CustomUserDetails;
import com.elice.spatz.domain.user.dto.*;
import com.elice.spatz.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    // 회원 가입 처리
    @PostMapping("/users")
    public ResponseEntity<UserRegisterResultDto> register(@RequestBody UserRegisterDto userRegisterDto) {
        String hashedPassword = passwordEncoder.encode(userRegisterDto.getPassword());
        userRegisterDto.setPassword(hashedPassword);

        UserRegisterResultDto result = userService.register(userRegisterDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }


    @PostMapping("/apiLogin")
    public ResponseEntity<SignInResponse> apiLogin(@Valid @RequestBody SignInRequest signInRequest) {

        SignInResponse signInResponse = userService.signIn(signInRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(signInResponse);
    }

    // 입력된 이메일에 해당하는 유저가 있는 지 여부를 체크하는 기능
    @PostMapping("/users/email")
    public ResponseEntity<Void> checkIfEnteredEmailAlreadyExist(@RequestBody Map<String, String> requestBody) {

        String email = requestBody.get("email");
        userService.findByEmail(email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 입력된 닉네임에 해당하는 유저가 있는 지 여부를 체크하는 기능
    @PostMapping("/users/nickname")
    public ResponseEntity<Void> checkIfEnteredNicknameAlreadyExist(@RequestBody Map<String, String> requestBody) {

        String nickname = requestBody.get("nickname");
        userService.findByNickname(nickname);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 유저가 입력한 비밀번호가 맞는 지 체크하는 함수
    @PostMapping("/users/password")
    public ResponseEntity<String> checkPasswordByUserId(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody Map<String, String> requestBody) {
        String password = requestBody.get("password");
        userService.checkPasswordByUserId(customUserDetails.getId(), password);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    // 비밀번호를 변경하는 함수
    @PatchMapping("/users/password")
    public ResponseEntity<String> updateUserInformation(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody PasswordChangeRequest passwordChangeRequest) {

        userService.changePassword(customUserDetails.getId(), passwordChangeRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    // 이메일을 변경하는 함수
    @PatchMapping("/users/email")
    public ResponseEntity<String> updateUserEmail(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody Map<String, String> requestBody) {

        String email = requestBody.get("email");
        userService.changeEmail(customUserDetails.getId(), email);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    // 닉네임을 변경하는 함수
    @PatchMapping("/users/nickname")
    public ResponseEntity<String> updateUserNickname(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody Map<String, String> requestBody) {

        String nickname = requestBody.get("nickname");
        userService.changeNickname(customUserDetails.getId(), nickname);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    // 계정 활성화 / 비활성화
    @PatchMapping("/users/activated")
    public ResponseEntity<String> updateUserActivationStatus(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody Map<String, String> requestBody) {

        String activated = requestBody.get("activated");
        boolean activationStatus;
        activationStatus = activated.equals("true");
        userService.updateActivation(customUserDetails.getId(), activationStatus);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    // 유저 삭제
    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        userService.deleteUser(customUserDetails.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }


    // 유저의 프로필 이미지 변경
    @PatchMapping("/users/profile")
    public ResponseEntity<String> postUserProfileImage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam("profileImage") MultipartFile multipartFile) throws IOException {

        String imgUrl = userService.updateUserProfileImage(customUserDetails.getId(), multipartFile);
        return ResponseEntity.status(HttpStatus.OK).body(imgUrl);
    }
}

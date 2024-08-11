package com.elice.spatz.domain.user.controller;

import com.amazonaws.Response;
import com.elice.spatz.config.CustomUserDetails;
import com.elice.spatz.domain.user.dto.*;
import com.elice.spatz.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<SignInResponse> apiLogin(@RequestBody SignInRequest signInRequest) {

        SignInResponse signInResponse = userService.signIn(signInRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(signInResponse);
    }

    // 이메일을 JSON BODY로 받아서, 해당하는 이메일에 해당하는 유저가 있는 지 여부를 체크하는 컨트롤러 기능
    @GetMapping("/users")
    public ResponseEntity<String> findUserByEmail(@RequestParam(required = false) String email, @RequestParam(required = false) String nickname) {

        if(email == null && nickname == null)
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
        else if(email != null)
            userService.findByEmail(email);
        else {
            userService.findByNickname(nickname);
        }


        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    // 비밀번호를 체크하는 함수
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

    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        userService.deleteUser(customUserDetails.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }
}

package com.elice.spatz.domain.user.controller;

import com.elice.spatz.domain.user.service.EmailService;
import com.elice.spatz.domain.user.service.MailVerificationCodeService;
import com.elice.spatz.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class AuthenticationMailController {

    private final EmailService emailService;
    private final MailVerificationCodeService mailVerificationCodeService;
    private final UserService userService;


    @PostMapping("/mails")
    public ResponseEntity<String> mailVeryficationCode(@RequestParam String email) {
        // 이메일 송신 후
        String code = generateRandom6DigitCode();
        emailService.sendEmail(email, "spatz application verification code", code);

        // 데이터베이스 저장
        mailVerificationCodeService.verificationCodeSave(email, code);

        return ResponseEntity.status(HttpStatus.CREATED).body(code);
    }

    // 사용자가 확인코드를 입력하고 인증 버튼을 눌렀을 때 동작하는 메소드
    @GetMapping("/mails")
    public ResponseEntity<String> checkVerificationCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        // 확인 코드를 검증하는 작업
        mailVerificationCodeService.validateCode(email, code);

        // 확인 코드를 검증한 후, 해당 이메일에 대한 비밀번호를 랜덤으로 설정
        String temporaryPassword = generateRandomPassword();
        userService.changeRandomPasswordByEmail(email, temporaryPassword);

        // 랜덤으로 설정한 비밀번호를 다시 이메일로 발송한다
        emailService.sendEmail(email, "spatz application : Temporary Password", temporaryPassword);
        System.out.println("completed!!");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

    // 6자리 숫자로 이루어진 확인 코드를 생성하는 메소드
    public String generateRandom6DigitCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            // 0부터 9까지의 숫자를 랜덤으로 생성
            int digit = random.nextInt(10);
            code.append(digit);
        }

        return code.toString();
    }

    // 10자리 임시 비밀번호를 생성하는 메소드
    public String generateRandomPassword() {
        String LOWER = "abcdefghijklmnopqrstuvwxyz";
        String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String DIGITS = "0123456789";
        String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";

        String ALL_CHARS = LOWER + UPPER + DIGITS + SPECIAL_CHARS;
        int PASSWORD_LENGTH = 10;
        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // 비밀번호를 구성하는 각 문자를 랜덤하게 선택
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(ALL_CHARS.length());
            password.append(ALL_CHARS.charAt(index));
        }

        return password.toString();
    }
}

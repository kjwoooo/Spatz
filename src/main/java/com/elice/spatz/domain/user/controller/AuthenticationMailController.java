package com.elice.spatz.domain.user.controller;

import com.elice.spatz.domain.user.service.EmailService;
import com.elice.spatz.domain.user.service.MailVerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequiredArgsConstructor
public class AuthenticationMailController {

    private final EmailService emailService;
    private final MailVerificationCodeService mailVerificationCodeService;


    @PostMapping("/mails")
    public ResponseEntity<String> mailVeryficationCode(@RequestParam String email) {
        // 이메일 송신 후
        String code = generateRandom6DigitCode();
        emailService.sendEmail(email, "spatz application verification code", code);

        // 데이터베이스 저장
        mailVerificationCodeService.verificationCodeSave(email, code);

        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }

    @GetMapping("/mails")
    public ResponseEntity<String> checkVerificationCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        mailVerificationCodeService.validateCode(email, code);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("");
    }

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
}

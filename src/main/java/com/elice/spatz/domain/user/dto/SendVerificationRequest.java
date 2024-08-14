package com.elice.spatz.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendVerificationRequest {

    @NotBlank(message = "이메일은 필수 입력사항입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
}

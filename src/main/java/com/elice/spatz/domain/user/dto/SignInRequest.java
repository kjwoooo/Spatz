package com.elice.spatz.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignInRequest {

    @NotBlank(message = "이메일은 필수 입력사항입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력사항입니다")
    private String password;
}

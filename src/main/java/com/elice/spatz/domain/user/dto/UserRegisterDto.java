package com.elice.spatz.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRegisterDto {

    @NotBlank(message = "이메일은 필수 입력사항입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력사항입니다")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력사항입니다")
    @Pattern(regexp = "^[a-zA-Z0-9]{2,8}$", message = "닉네임은 최소 2자에서 최대 8자의 영문자와 숫자만 허용됩니다.")
    private String nickname;

    private boolean marketingAgreed;
}

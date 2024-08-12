package com.elice.spatz.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponse {
    private String username;
    private String profileImage;
    private String accessToken;
    private String refreshToken;
}

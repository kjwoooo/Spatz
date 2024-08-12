package com.elice.spatz.domain.userfeature.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String nickname;
}

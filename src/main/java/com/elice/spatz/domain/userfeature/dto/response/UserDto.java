package com.elice.spatz.domain.userfeature.dto.response;

import com.elice.spatz.domain.user.entity.UsersProfileImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String nickname;
    private String role;
    private String imageUrl;
}

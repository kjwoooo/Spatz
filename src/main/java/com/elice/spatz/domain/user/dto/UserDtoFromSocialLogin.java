package com.elice.spatz.domain.user.dto;

import lombok.Data;

@Data
public class UserDtoFromSocialLogin {
    // 로그인한 사용자의 아이디
    private Long id;

    // 로그인한 사용자의 역할
    private String role;

    // 로그인한 사용자의 아이디
    private String name;

    // 로그인한 사용자의 이메일
    private String email;

    // 로그인한 사용자의 아이디를 바탕으로, 우리 서버에서 관리할 유저의 이름
    private String username;
}

package com.elice.spatz.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailVerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String code;

    public MailVerificationCode(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public void changeCode(String code) {
        this.code = code;
    }
}

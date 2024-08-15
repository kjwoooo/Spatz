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

    private boolean verified;

    public MailVerificationCode(String email, String code) {
        this.email = email;
        this.code = code;
        this.verified = false;
    }

    public void changeCode(String code) {
        this.code = code;
    }
    public void verify () {
        this.verified = true;
    }
}

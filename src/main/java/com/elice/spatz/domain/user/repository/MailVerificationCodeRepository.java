package com.elice.spatz.domain.user.repository;

import com.elice.spatz.domain.user.entity.MailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailVerificationCodeRepository extends JpaRepository<MailVerificationCode, Long> {

    Optional<MailVerificationCode> findByEmail(String email);
}

package com.elice.spatz.domain.user.service;

import com.elice.spatz.domain.user.entity.MailVerificationCode;
import com.elice.spatz.domain.user.repository.MailVerificationCodeRepository;
import com.elice.spatz.exception.errorCode.UserErrorCode;
import com.elice.spatz.exception.exception.UserException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailVerificationCodeService {

    private final MailVerificationCodeRepository mailVerificationCodeRepository;

    @Transactional
    public void verificationCodeSave(String email, String code) {
        // 이미 특정 이메일에 해당하는 확인 코드가 존재한다면 덮어씌우기
        // 그렇지 않다면 새로 저장
        mailVerificationCodeRepository.findByEmail(email).ifPresentOrElse(
                verificaionCode -> verificaionCode.changeCode(code),
                () -> mailVerificationCodeRepository.save(new MailVerificationCode(email, code))
        );


    }

    public void validateCode(String email, String code) {
        MailVerificationCode mailVerificationCode = mailVerificationCodeRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.INVALID_VERIFICATION_CODE));

        if(!mailVerificationCode.getCode().equals(code)) {
            throw new UserException(UserErrorCode.INVALID_VERIFICATION_CODE);
        }

    }
}

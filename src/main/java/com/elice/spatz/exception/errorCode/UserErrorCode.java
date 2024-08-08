package com.elice.spatz.exception.errorCode;

import com.elice.spatz.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    // 일반 유저 관련 예외상황
    EMAIL_ALREADY_IN_USE(HttpStatus.BAD_REQUEST, "이미 사용 중인 사용자 이메일입니다.", "U001"),
    EMAIL_RESIGN_IN_USE(HttpStatus.BAD_REQUEST, "탈퇴한 사용자 이메일입니다.", "U002"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "U003"),
    EMAIL_VERIFICATION_NOT_COMPLETE(HttpStatus.UNAUTHORIZED, "이메일 인증이 완료되지 않았습니다.", "U004"),
    NICKNAME_ALREADY_IN_USE(HttpStatus.CONFLICT, "이미 사용 중인 사용자 닉네임입니다.", "U005"),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "확인코드가 일치하지 않습니다", "U006"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다", "U007"),

    // 인증 관련 예외 상황
    NO_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "JWT 토큰이 존재하지 않습니다", "AU001"),
    TAMPERED_TOKEN(HttpStatus.BAD_REQUEST, "JWT 토큰이 임의로 변경되었습니다", "AU002");



    private final HttpStatus status;
    private final String message;
    private final String code;

    @Override
    public HttpStatus getHttpStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

}

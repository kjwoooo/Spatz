package com.elice.spatz.exception.errorCode;

import com.elice.spatz.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VoiceChannelErrorCode implements ErrorCode {
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "음성 채널을 찾을 수 없습니다.", "V001"),
    SERVER_NOT_FOUND(HttpStatus.NOT_FOUND, "서버를 찾을 수 없습니다.", "V002"),
    INVALID_CHANNEL_NAME(HttpStatus.BAD_REQUEST, "유효하지 않은 채널 이름입니다.", "V003");

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
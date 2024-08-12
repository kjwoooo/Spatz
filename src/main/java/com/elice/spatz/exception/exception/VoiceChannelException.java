package com.elice.spatz.exception.exception;

import com.elice.spatz.exception.errorCode.VoiceChannelErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class VoiceChannelException extends RuntimeException {
    private final VoiceChannelErrorCode voiceChannelErrorCode;
}
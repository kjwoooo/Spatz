package com.elice.spatz.exception.exception;


import com.elice.spatz.exception.errorCode.ChannelErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChannelException extends RuntimeException {
    private final ChannelErrorCode channelErrorCode;
}
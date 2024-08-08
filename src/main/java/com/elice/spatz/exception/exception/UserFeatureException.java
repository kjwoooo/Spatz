package com.elice.spatz.exception.exception;

import com.elice.spatz.exception.errorCode.UserFeatureErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserFeatureException extends RuntimeException{
    private final UserFeatureErrorCode userFeatureErrorCode;
}

package com.woolog.exception;

import com.woolog.response.CommonResponseField;
import com.woolog.response.ResponseStatus;
import org.springframework.security.core.AuthenticationException;

public abstract class SecurityException extends AuthenticationException {

    private static final String MESSAGE = "인증되지 않은 사용자 입니다.";

    public SecurityException() {
        super(MESSAGE);
    }

    public SecurityException(String message) {
        super(message);
    }

    public abstract ResponseStatus getHttpStatus();

    public abstract CommonResponseField getErrorResponse();
}

package com.woolog.exception;

import org.springframework.security.core.AuthenticationException;

public class MemberAuthenticationException extends AuthenticationException implements CustomException {

    private static final String MESSAGE = "인증정보가 일치하지 않습니다.";

    public MemberAuthenticationException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

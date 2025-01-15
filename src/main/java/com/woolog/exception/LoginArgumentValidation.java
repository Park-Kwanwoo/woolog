package com.woolog.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginArgumentValidation extends AuthenticationException implements CustomException {

    private static final String MESSAGE = "아이디나 비밀번호를 입력해주세요.";

    public LoginArgumentValidation() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

package com.woolog.exception;

import org.springframework.security.access.AccessDeniedException;

public class AuthorizeException extends AccessDeniedException implements CustomException {

    private static final String MESSAGE = "접근 권한이 없습니다.";

    public AuthorizeException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

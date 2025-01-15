package com.woolog.exception;

import io.jsonwebtoken.JwtException;

public class JwtValidException extends JwtException implements CustomException {
    private static final String MESSAGE = "유효하지 않은 토큰입니다.";

    public JwtValidException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

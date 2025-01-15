package com.woolog.exception;

public class InvalidTokenException extends WoologException implements CustomException {

    private static final String MESSAGE = "토큰 값이 존재하지 않습니다.";

    public InvalidTokenException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

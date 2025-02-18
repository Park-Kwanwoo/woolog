package com.woolog.exception;

public class InvalidPassword extends WoologException implements CustomException {

    private static final String MESSAGE = "잘못된 비밀번호 값입니다.";

    public InvalidPassword() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

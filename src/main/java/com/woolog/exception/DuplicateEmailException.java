package com.woolog.exception;

public class DuplicateEmailException extends WoologException implements CustomException {

    private static final String MESSAGE = "중복된 이메일입니다.";

    public DuplicateEmailException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
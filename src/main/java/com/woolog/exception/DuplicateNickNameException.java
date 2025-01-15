package com.woolog.exception;

public class DuplicateNickNameException extends WoologException implements CustomException {

    private static final String MESSAGE = "중복된 닉네임입니다.";

    public DuplicateNickNameException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

package com.woolog.exception;

public class MemberInfoNotValidException extends WoologException implements CustomException {

    private static final String MESSAGE = "회원정보가 일치하지 않습니다.";

    public MemberInfoNotValidException() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

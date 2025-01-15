package com.woolog.exception;

public class MemberNotExist extends WoologException implements CustomException {

    private static final String MESSAGE = "존재하지 않는 회원정보입니다.";

    public MemberNotExist() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}

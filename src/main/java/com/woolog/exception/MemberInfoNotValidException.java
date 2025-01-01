package com.woolog.exception;

import com.woolog.response.ExceptionResponseData;
import com.woolog.response.ResponseStatus;

public class MemberInfoNotValidException extends WoologException implements CustomException {

    private static final String MESSAGE = "회원정보가 일치하지 않습니다.";

    public MemberInfoNotValidException() {
        super(MESSAGE);
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return null;
    }

    @Override
    public ExceptionResponseData getErrorResponse() {
        return null;
    }
}

package com.woolog.exception;

import com.woolog.response.ExceptionResponseData;
import com.woolog.response.ResponseStatus;

public class MemberNotExist extends WoologException implements CustomException {

    private final String field;
    private final String message;


    public MemberNotExist(String field, String message) {
        super(message);
        this.field = field;
        this.message = message;
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return ResponseStatus.MEMBER_NOT_EXIST;
    }

    @Override
    public ExceptionResponseData getErrorResponse() {
        return ExceptionResponseData.builder()
                .field(this.field)
                .message(this.message)
                .build();
    }
}

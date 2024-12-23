package com.woolog.exception;

import com.woolog.response.ExceptionResponseData;
import com.woolog.response.ResponseStatus;

public class DuplicateNickNameException extends WoologException implements CustomException {

    private final String field;
    private final String message;

    public DuplicateNickNameException(String field, String message) {
        super(message);
        this.field = field;
        this.message = message;
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return ResponseStatus.DUPLICATE_NICKNAME_EXCEPTION;
    }

    @Override
    public ExceptionResponseData getErrorResponse() {
        return ExceptionResponseData.builder()
                .field(this.field)
                .message(this.message)
                .build();
    }
}

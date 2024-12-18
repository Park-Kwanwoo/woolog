package com.woolog.exception;

import com.woolog.response.CommonResponseField;
import com.woolog.response.ResponseStatus;

public class DuplicateEmailException extends WoologException implements CustomException {

    private final String field;
    private final String message;

    public DuplicateEmailException(String field, String message) {
        super(message);
        this.field = field;
        this.message = message;
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return ResponseStatus.DUPLICATE_EMAIL_EXCEPTION;
    }

    @Override
    public CommonResponseField getErrorResponse() {
        return CommonResponseField.builder()
                .field(this.field)
                .message(this.message)
                .build();
    }
}
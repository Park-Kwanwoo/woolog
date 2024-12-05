package com.woolog.exception;


import com.woolog.response.CommonResponseField;
import com.woolog.response.ResponseStatus;
import lombok.Getter;

@Getter
public class UserNotFoundException extends WoologException {

    private final String field;
    private final String message;

    public UserNotFoundException(String field, String message) {
        super();
        this.field = field;
        this.message = message;
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return ResponseStatus.NOT_FOUND;
    }

    @Override
    public CommonResponseField getErrorResponse() {
        return CommonResponseField.builder()
                .field(this.field)
                .message(this.message)
                .build();
    }
}

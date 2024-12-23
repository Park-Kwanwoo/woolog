package com.woolog.exception;

import com.woolog.response.ExceptionResponseData;
import com.woolog.response.ResponseStatus;
import lombok.Getter;

/**
 * status: 404
 */
@Getter
public class PostNotFound extends WoologException implements CustomException {

    private final String field;
    private final String message;

    public PostNotFound(String field, String message) {
        super();
        this.field = field;
        this.message = message;
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return ResponseStatus.NOT_FOUND;
    }

    @Override
    public ExceptionResponseData getErrorResponse() {
        return ExceptionResponseData.builder()
                .field(this.field)
                .message(this.message)
                .build();
    }
}

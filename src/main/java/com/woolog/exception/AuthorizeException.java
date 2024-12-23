package com.woolog.exception;

import com.woolog.response.ExceptionResponseData;
import com.woolog.response.ResponseStatus;
import org.springframework.security.access.AccessDeniedException;

public class AuthorizeException extends AccessDeniedException implements CustomException {

    private static final String MESSAGE = "권한이 없습니다.";

    public AuthorizeException() {
        super(MESSAGE);
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return ResponseStatus.AUTHORIZE_EXCEPTION;
    }

    @Override
    public ExceptionResponseData getErrorResponse() {
        return ExceptionResponseData
                .builder()
                .build();
    }
}

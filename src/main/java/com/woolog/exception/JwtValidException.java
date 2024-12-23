package com.woolog.exception;

import com.woolog.response.ExceptionResponseData;
import com.woolog.response.ResponseStatus;
import io.jsonwebtoken.JwtException;

public class JwtValidException extends JwtException implements CustomException {
    private static final String MESSAGE = "유효하지 않은 토큰입니다.";

    public JwtValidException() {
        super(MESSAGE);
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return ResponseStatus.JWT_VERIFY_EXCEPTION;
    }

    @Override
    public ExceptionResponseData getErrorResponse() {
        return ExceptionResponseData
                .builder()
                .build();
    }
}

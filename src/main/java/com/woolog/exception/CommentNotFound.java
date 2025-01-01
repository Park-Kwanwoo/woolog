package com.woolog.exception;

import com.woolog.response.ExceptionResponseData;
import com.woolog.response.ResponseStatus;

public class CommentNotFound extends WoologException implements CustomException {

    private static final String MESSAGE = "존재하지 않는 댓글입니다.";

    public CommentNotFound() {
        super(MESSAGE);
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return ResponseStatus.NOT_FOUND;
    }

    @Override
    public ExceptionResponseData getErrorResponse() {
        return ExceptionResponseData.builder()
                .build();
    }
}

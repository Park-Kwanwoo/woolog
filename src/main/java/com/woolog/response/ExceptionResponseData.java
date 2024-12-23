package com.woolog.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ExceptionResponseData {

    private final String field;
    private final String message;

    @Builder
    public ExceptionResponseData(String field, String message) {
        this.field = field;
        this.message = message;
    }
}

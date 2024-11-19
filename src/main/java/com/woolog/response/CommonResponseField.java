package com.woolog.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommonResponseField {

    private final String field;
    private final String message;

    @Builder
    public CommonResponseField(String field, String message) {
        this.field = field;
        this.message = message;
    }
}

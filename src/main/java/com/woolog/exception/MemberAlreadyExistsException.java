package com.woolog.exception;

import com.woolog.response.CommonResponseField;
import com.woolog.response.ResponseStatus;
import lombok.Getter;

@Getter
public class MemberAlreadyExistsException extends WoologException {

    private static final String MESSAGE = "이미 존재하는 이메일입니다.";
    private final String field;
    private final String message;

    public MemberAlreadyExistsException(String field, String message1) {
        super(MESSAGE);
        this.field = field;
        this.message = message1;
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return ResponseStatus.MEMBER_ALREADY_EXISTS;
    }

    @Override
    public CommonResponseField getErrorResponse() {
        return CommonResponseField.builder()
                .field(this.field)
                .message(this.message)
                .build();
    }
}

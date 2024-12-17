package com.woolog.exception;

import com.woolog.response.CommonResponseField;
import com.woolog.response.ResponseStatus;
import org.springframework.security.core.AuthenticationException;

public class MemberAuthenticationException extends AuthenticationException implements CustomException {

    private final String field;
    private final String message;

    public MemberAuthenticationException(String field, String message) {
        super(message);
        this.field = field;
        this.message = message;
    }

    @Override
    public ResponseStatus getHttpStatus() {
        return ResponseStatus.MEMBER_AUTHENTICATION_EXCEPTION;
    }

    @Override
    public CommonResponseField getErrorResponse() {
        return CommonResponseField.builder()
                .field(this.field)
                .message(this.message)
                .build();
    }
}

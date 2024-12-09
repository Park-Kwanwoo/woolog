package com.woolog.exception;


import com.woolog.response.CommonResponseField;
import com.woolog.response.ResponseStatus;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class MemberNotExistException extends AuthenticationException {

    private final String field;
    private final String message;

    public MemberNotExistException(String field, String message) {
        super(message);
        this.field = field;
        this.message = message;
    }

    public ResponseStatus getHttpStatus() {
        return ResponseStatus.NOT_FOUND;
    }

    public CommonResponseField getErrorResponse() {
        return CommonResponseField.builder()
                .field(this.field)
                .message(this.message)
                .build();
    }
}

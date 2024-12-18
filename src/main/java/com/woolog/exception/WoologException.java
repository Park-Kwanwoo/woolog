package com.woolog.exception;

import com.woolog.response.CommonResponseField;
import com.woolog.response.ResponseStatus;

public abstract class WoologException extends RuntimeException {

    public WoologException() {
        super();
    }

    public WoologException(String message) {
        super(message);
    }

}

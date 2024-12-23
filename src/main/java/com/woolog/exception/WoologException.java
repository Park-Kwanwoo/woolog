package com.woolog.exception;

public abstract class WoologException extends RuntimeException {

    public WoologException() {
        super();
    }

    public WoologException(String message) {
        super(message);
    }

}

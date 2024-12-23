package com.woolog.exception;

import com.woolog.response.ExceptionResponseData;
import com.woolog.response.ResponseStatus;

public interface CustomException {

    ResponseStatus getHttpStatus();

    ExceptionResponseData getErrorResponse();
}

package com.woolog.exception;

import com.woolog.response.CommonResponseField;
import com.woolog.response.ResponseStatus;

public interface CustomException {

    ResponseStatus getHttpStatus();

    CommonResponseField getErrorResponse();
}

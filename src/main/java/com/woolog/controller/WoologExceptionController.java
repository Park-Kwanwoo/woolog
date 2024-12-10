package com.woolog.controller;

import com.woolog.exception.SecurityException;
import com.woolog.exception.WoologException;
import com.woolog.response.CommonResponse;
import com.woolog.response.CommonResponseField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.woolog.response.ResponseStatus.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class WoologExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<CommonResponseField>> validExceptionHandler(MethodArgumentNotValidException e) {

        CommonResponse<CommonResponseField> commonResponse = new CommonResponse<>(BAD_REQUEST);
        List<FieldError> fieldErrors = e.getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            commonResponse.addData(new CommonResponseField(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        return ResponseEntity.status(commonResponse.getStatus())
                .body(commonResponse);
    }

    @ExceptionHandler(WoologException.class)
    public ResponseEntity<CommonResponse<CommonResponseField>> woologExceptionHandler(WoologException e) {

        CommonResponse<CommonResponseField> commonResponse = new CommonResponse<>(e.getHttpStatus());
        commonResponse.addData(e.getErrorResponse());

        return ResponseEntity.status(commonResponse.getStatus())
                .body(commonResponse);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<CommonResponse<CommonResponseField>> testHandler(SecurityException e) {

        CommonResponse<CommonResponseField> commonResponse = new CommonResponse<>(e.getHttpStatus());
        commonResponse.addData(e.getErrorResponse());

        return ResponseEntity.status(commonResponse.getStatus())
                .body(commonResponse);
    }
}
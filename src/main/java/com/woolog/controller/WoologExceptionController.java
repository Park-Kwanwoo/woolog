package com.woolog.controller;

import com.woolog.exception.WoologException;
import com.woolog.response.CommonResponse;
import com.woolog.response.CommonResponseField;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.woolog.response.ResponseStatus.BAD_REQUEST;

@RestControllerAdvice
public class WoologExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<CommonResponseField>> validExceptionHandler(MethodArgumentNotValidException e) {

        CommonResponse<CommonResponseField> commonResponse = new CommonResponse<>(BAD_REQUEST);
        List<FieldError> fieldErrors = e.getFieldErrors();

        for (FieldError fieldError  : fieldErrors) {
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
}
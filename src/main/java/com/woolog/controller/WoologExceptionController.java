package com.woolog.controller;

import com.woolog.exception.CustomException;
import com.woolog.exception.WoologException;
import com.woolog.response.CommonResponse;
import com.woolog.response.CommonResponseField;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
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
    public CommonResponse<CommonResponseField> validExceptionHandler(MethodArgumentNotValidException e) {

        CommonResponse<CommonResponseField> commonResponse = new CommonResponse<>(BAD_REQUEST);
        List<FieldError> fieldErrors = e.getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            commonResponse.addData(new CommonResponseField(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        return commonResponse;
    }

    @ExceptionHandler(value = {WoologException.class, AuthenticationException.class, JwtException.class, AccessDeniedException.class})
    public CommonResponse<CommonResponseField> woologExceptionHandler(CustomException e) {

        CommonResponse<CommonResponseField> commonResponse = new CommonResponse<>(e.getHttpStatus());
        commonResponse.addData(e.getErrorResponse());

        return commonResponse;
    }
}
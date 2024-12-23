package com.woolog.controller;

import com.woolog.exception.CustomException;
import com.woolog.exception.WoologException;
import com.woolog.response.ApiResponse;
import com.woolog.response.ExceptionResponseData;
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
    public ApiResponse<ExceptionResponseData> validExceptionHandler(MethodArgumentNotValidException e) {

        ApiResponse<ExceptionResponseData> apiResponse = new ApiResponse<>(BAD_REQUEST);
        List<FieldError> fieldErrors = e.getFieldErrors();

        for (FieldError fieldError : fieldErrors) {
            apiResponse.addData(new ExceptionResponseData(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        return apiResponse;
    }

    @ExceptionHandler(value = {WoologException.class, AuthenticationException.class, JwtException.class, AccessDeniedException.class})
    public ApiResponse<ExceptionResponseData> woologExceptionHandler(CustomException e) {

        ApiResponse<ExceptionResponseData> apiResponse = new ApiResponse<>(e.getHttpStatus());
        apiResponse.addData(e.getErrorResponse());

        return apiResponse;
    }
}
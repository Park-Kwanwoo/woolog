package com.woolog.controller;

import com.woolog.exception.CustomException;
import com.woolog.exception.WoologException;
import com.woolog.response.ApiResponse;
import com.woolog.response.ExceptionResponseData;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ApiResponse<?> validExceptionHandler(MethodArgumentNotValidException e) {
        return ApiResponse.errorWithBingResult(e);
    }

    @ExceptionHandler(value = {WoologException.class, AuthenticationException.class, JwtException.class, AccessDeniedException.class})
    public ApiResponse<?> woologExceptionHandler(CustomException e) {
        return ApiResponse.error(e.getMessage());
    }
}
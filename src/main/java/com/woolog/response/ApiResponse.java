package com.woolog.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ApiResponse<T> {

    private static final String SUCCESS_STATUS = "SUCCESS";
    private static final String FAIL_STATUS = "FAIL";
    private static final String ERROR_STATUS = "ERROR";


    private String statusCode;
    private String message;
    private final T data;

    public ApiResponse(String statusCode, String message, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> successWithContent(T data) {
        return new ApiResponse<>(SUCCESS_STATUS, null, data);
    }

    public static ApiResponse<?> errorWithBingResult(BindingResult bindingResult) {

        List<ExceptionResponseData> errors = new ArrayList<>();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.add(new ExceptionResponseData(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        return new ApiResponse<>(ERROR_STATUS, null, errors);
    }

    public static ApiResponse<?> error(String message) {
        return new ApiResponse<>(ERROR_STATUS, message, null);
    }
}

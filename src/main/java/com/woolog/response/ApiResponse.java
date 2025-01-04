package com.woolog.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ApiResponse<T> {

    private int code;
    private String message;
    private final List<T> data = new ArrayList<>();

    public ApiResponse(ResponseStatus responseStatus) {
        this.code = responseStatus.getCode();
        this.message = responseStatus.getMessage();
    }

    @Builder
    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public void addData(T e) {
        data.add(e);
    }
}

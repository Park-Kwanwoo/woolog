package com.woolog.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class ApiResponse<T> {

    private int status;
    private String message;
    private final List<T> data = new ArrayList<>();

    public ApiResponse(ResponseStatus responseStatus) {
        this.status = responseStatus.getStatus();
        this.message = responseStatus.getMessage();
    }

    @Builder
    public ApiResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public void addData(T e) {
        data.add(e);
    }
}

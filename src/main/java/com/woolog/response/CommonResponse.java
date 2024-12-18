package com.woolog.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CommonResponse<T> {

    private int status;
    private String code;
    private String message;
    private final List<T> data = new ArrayList<>();

    public CommonResponse(ResponseStatus responseStatus) {
        this.status = responseStatus.getStatus();
        this.code = responseStatus.getCode();
        this.message = responseStatus.getMessage();
    }

    public void addData(T e) {
        data.add(e);
    }
}

package com.woolog.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus {

    /**
     * status : int
     * code: String
     * description: String
     */

    // HTTP STATUS
    BAD_REQUEST(400, "BAD_REQUEST", "잘못된 입력 값입니다."),
    NOT_FOUND(404, "NOT_FOUND", "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버에러");

    // WOOLOG ERROR CODE
    private final int status;
    private final String code;
    private final String description;

}

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
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버에러"),

    // CUSTOM STATUS
    MEMBER_AUTHENTICATION_EXCEPTION(401, "MEMBER_AUTHENTICATION_EXCEPTION", "존재하지 않는 회원정보입니다."),
    MEMBER_ALREADY_EXISTS(401,"MEMBER_ALREADY_EXISTS","이미 존재하는 이메일 입니다.")
    ;

    // WOOLOG ERROR CODE
    private final int status;
    private final String code;
    private final String description;
}

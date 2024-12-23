package com.woolog.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseStatus {

    /**
     * status : int
     * message: String
     */

    // HTTP STATUS
    BAD_REQUEST(400, "잘못된 입력 값입니다."),
    NOT_FOUND(404, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(500, "서버에러"),

    // CUSTOM STATUS
    MEMBER_AUTHENTICATION_EXCEPTION(401, "잘못된 회원정보입니다."),
    DUPLICATE_EMAIL_EXCEPTION(401,"이미 존재하는 이메일입니다."),
    DUPLICATE_NICKNAME_EXCEPTION(401,"이미 존재하는 닉네임입니다."),
    MEMBER_NOT_EXIST(401,"존재하지 않는 회원입니다."),
    JWT_VERIFY_EXCEPTION(401,"유효하지 않은 토큰입니다."),
    AUTHORIZE_EXCEPTION(401,"접근 권한이 없습니다.")
    ;

    // WOOLOG ERROR CODE
    private final int status;
    private final String message;
}

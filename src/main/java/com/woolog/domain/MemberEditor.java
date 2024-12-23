package com.woolog.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberEditor {

    private final String password;
    private final String nickName;

    @Builder
    public MemberEditor(String password, String nickName) {
        this.password = password;
        this.nickName = nickName;
    }
}

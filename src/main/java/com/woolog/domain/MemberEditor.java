package com.woolog.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberEditor {

    private final String password;
    private final String nickname;

    @Builder
    public MemberEditor(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
}

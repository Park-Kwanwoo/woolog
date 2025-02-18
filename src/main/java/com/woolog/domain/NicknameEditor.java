package com.woolog.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class NicknameEditor {

    private final String nickname;

    @Builder
    public NicknameEditor(String nickname) {
        this.nickname = nickname;
    }
}

package com.woolog.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberEdit {

    @NotBlank(message = "{NotBlank.password}")
    private final String password;

    @NotBlank(message = "{NotBlank.nickname}")
    private final String nickname;

    @Builder
    public MemberEdit(String password, String nickname) {
        this.password = password;
        this.nickname = nickname;
    }
}

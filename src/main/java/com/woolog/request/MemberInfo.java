package com.woolog.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfo {

    @NotBlank(message = "{NotBlank.hashId}")
    private final String hashId;

    @NotBlank(message = "{NotBlank.password}")
    private final String password;

    @Builder
    public MemberInfo(String hashId, String password) {
        this.hashId = hashId;
        this.password = password;
    }
}

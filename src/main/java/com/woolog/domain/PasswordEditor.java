package com.woolog.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PasswordEditor {

    private final String password;

    @Builder
    public PasswordEditor(String password) {
        this.password = password;
    }
}

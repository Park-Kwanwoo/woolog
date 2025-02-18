package com.woolog.request.member;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PasswordEdit {

    @NotBlank(message = "{NotBlank.password}")
    private final String password;

    @Builder
    @JsonCreator
    public PasswordEdit(@JsonProperty("password") String password) {
        this.password = password;
    }
}

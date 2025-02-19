package com.woolog.request.member;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailCheck {

    @NotBlank(message = "{NotBlank.email}")
    private final String email;

    @Builder
    @JsonCreator
    public EmailCheck(@JsonProperty("email") String email) {
        this.email = email;
    }
}

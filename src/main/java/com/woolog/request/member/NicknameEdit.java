package com.woolog.request.member;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class NicknameEdit {

    @NotBlank(message = "{NotBlank.nickname}")
    private final String nickname;

    @Builder
    @JsonCreator
    public NicknameEdit(@JsonProperty("nickname") String nickname) {
        this.nickname = nickname;
    }
}

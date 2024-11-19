package com.woolog.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostEdit {

    @NotBlank(message = "{NotBlank.title}")
    private final String title;

    @NotBlank(message = "{NotBlank.content}")
    private final String content;

    @Builder
    public PostEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

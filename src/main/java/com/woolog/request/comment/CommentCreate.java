package com.woolog.request.comment;

import com.woolog.domain.Comment;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
public class CommentCreate {

    @NotBlank(message = "{NotBlank.content}")
    private final String content;

    @Builder
    @Jacksonized
    public CommentCreate(String content) {
        this.content = content;
    }

    public Comment toComment() {
        return Comment.builder()
                .content(this.content)
                .build();
    }
}

package com.woolog.request.post;

import com.woolog.domain.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostCreate {

    @NotBlank(message = "{NotBlank.title}")
    private final String title;

    @NotBlank(message = "{NotBlank.content}")
    private final String content;

    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public Post toPost() {
        return Post.builder()
                .title(this.title)
                .content(this.content)
                .build();
    }
}

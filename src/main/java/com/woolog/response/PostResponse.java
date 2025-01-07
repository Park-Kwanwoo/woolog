package com.woolog.response;

import com.woolog.domain.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String nickname;
    private final LocalDateTime create_at;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.create_at = post.getCreatedAt();
        this.nickname = post.getMember().getNickname();
    }

    @Builder
    public PostResponse(Long id, String title, String content, String nickname, LocalDateTime create_at) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.create_at = create_at;
    }

}

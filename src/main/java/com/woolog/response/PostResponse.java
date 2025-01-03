package com.woolog.response;

import com.woolog.domain.Post;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String nickname;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.nickname = post.getMember().getNickname();
    }

    @Builder
    public PostResponse(Long id, String title, String content, String nickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
    }

}

package com.woolog.response;

import com.woolog.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final String email;
    private final String name;
    private final String nickname;

    @Builder
    public MemberResponse(String email, String name, String nickname) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .build();
    }
}

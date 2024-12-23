package com.woolog.response;

import com.woolog.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final String email;
    private final String name;
    private final String nickName;

    @Builder
    public MemberResponse(String email, String name, String nickName) {
        this.email = email;
        this.name = name;
        this.nickName = nickName;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .name(member.getName())
                .nickName(member.getNickName())
                .build();
    }
}

package com.woolog.response;

import com.woolog.domain.Member;
import com.woolog.domain.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final String email;
    private final String name;
    private final String nickname;
    private final boolean isAdmin;

    @Builder
    public MemberResponse(String email, String name, String nickname, boolean isAdmin) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.isAdmin = isAdmin;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .isAdmin(member.getRole().equals(Role.ADMIN))
                .build();
    }
}

package com.woolog.response;

import com.woolog.domain.Member;
import com.woolog.domain.Role;
import lombok.Builder;
import lombok.Getter;

import static com.woolog.domain.Role.ADMIN;

@Getter
public class MemberResponse {

    private final String email;
    private final String name;
    private final String nickname;
    private final Boolean admin;
    private final Boolean isMember;

    @Builder
    public MemberResponse(String email, String name, String nickname, boolean admin, boolean isMember) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.admin = admin;
        this.isMember = isMember;
    }

    public static MemberResponse of(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .admin(member.getRole().equals(ADMIN))
                .isMember(true)
                .build();
    }
}

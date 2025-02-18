package com.woolog.request.member;

import com.woolog.domain.Member;
import com.woolog.domain.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class Signup {

    @NotBlank(message = "{NotBlank.email}")
    private final String email;

    @NotBlank(message = "{NotBlank.name}")
    private final String name;

    @NotBlank(message = "{NotBlank.password}")
    private final String password;

    @NotBlank(message = "{NotBlank.nickname}")
    private final String nickname;


    @Builder
    public Signup(String email, String name, String password, String nickname) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.nickname = nickname;
    }

    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(this.email)
                .name(this.name)
                .nickname(this.nickname)
                .password(passwordEncoder.encode(this.password))
                .role(Role.MEMBER)
                .build();
    }
}

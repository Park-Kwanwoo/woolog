package com.woolog.request;

import com.woolog.config.HashEncrypt;
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
    private final String nickName;


    @Builder
    public Signup(String email, String name, String password, String nickName) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.nickName = nickName;
    }

    public Member toMember(PasswordEncoder passwordEncoder, HashEncrypt hashEncrypt) {
        return Member.builder()
                .email(this.email)
                .name(this.name)
                .nickName(this.nickName)
                .password(passwordEncoder.encode(this.password))
                .hashId(hashEncrypt.encrypt(this.email))
                .role(Role.MEMBER)
                .build();
    }
}

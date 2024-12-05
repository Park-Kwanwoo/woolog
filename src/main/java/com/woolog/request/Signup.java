package com.woolog.request;

import com.woolog.domain.Member;
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

    @Builder
    public Signup(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
    }

    public Member toMember(PasswordEncoder passwordEncoder) {
        return Member.builder()
                .email(this.email)
                .name(this.name)
                .password(passwordEncoder.encode(this.password))
                .build();
    }
}

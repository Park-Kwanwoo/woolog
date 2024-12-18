package com.woolog.security.handler;

import com.woolog.domain.Member;
import com.woolog.exception.MemberAuthenticationException;
import com.woolog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Member member = memberRepository.findMemberByEmail((String) authentication.getPrincipal())
                .orElseThrow(() -> new MemberAuthenticationException("MEMBER", "존재하지 않는 사용자입니다."));

        String email = member.getEmail();
        String encodedPassword = member.getPassword();
        String rawPassword = (String) authentication.getCredentials();
        String role = member.getRole().name();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role);

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new MemberAuthenticationException("MEMBER", "아이디나 비밀번호가 잘못되었습니다.");
        }

        return new UsernamePasswordAuthenticationToken(email, encodedPassword, Collections.singletonList(simpleGrantedAuthority));
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

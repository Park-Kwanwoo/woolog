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

        Member member = memberRepository.findByEmail((String) authentication.getPrincipal())
                .orElseThrow(MemberAuthenticationException::new);

        String email = member.getEmail();
        String encodedPassword = member.getPassword();
        String rawPassword = (String) authentication.getCredentials();
        String role = member.getRole().name();

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(role);

        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new MemberAuthenticationException();
        }

        return new UsernamePasswordAuthenticationToken(email, encodedPassword, Collections.singletonList(simpleGrantedAuthority));
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

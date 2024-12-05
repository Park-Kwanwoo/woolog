package com.woolog.security;

import com.woolog.domain.Member;
import com.woolog.exception.UserNotFoundException;
import com.woolog.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Member member = memberRepository.findMemberByEmail((String) authentication.getPrincipal())
                .orElseThrow(() -> new UserNotFoundException("USER", "존재하지 않는 사용자 입니다."));

        String email = member.getEmail();
        String encodedPassword = member.getPassword();
        String rawPassword = (String) authentication.getCredentials();

        if (passwordEncoder.matches(rawPassword, encodedPassword)) {
            return new UsernamePasswordAuthenticationToken(email, encodedPassword);
        } else {
            throw new AuthenticationServiceException("인증되지 않은 사용자입니다.");
        }
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

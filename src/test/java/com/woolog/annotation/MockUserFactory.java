package com.woolog.annotation;

import com.woolog.domain.Member;
import com.woolog.domain.Role;
import com.woolog.repository.member.MemberRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MockUserFactory implements WithSecurityContextFactory<CustomWithMockUser> {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MockUserFactory(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public SecurityContext createSecurityContext(CustomWithMockUser withMockUser) {

        String email = withMockUser.email();
        String password = withMockUser.password();
        String nickname = withMockUser.nickname();
        String name = withMockUser.name();
        String role = withMockUser.role();

        Member member = Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .nickname(nickname)
                .role(Role.valueOf(Role.class, role))
                .build();

        memberRepository.save(member);

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority(member.getRole().name()));

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(member.getEmail(), member.getPassword(), grantedAuthorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);

        return context;
    }
}

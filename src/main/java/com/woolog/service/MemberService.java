package com.woolog.service;

import com.woolog.domain.Member;
import com.woolog.exception.MemberAlreadyExistsException;
import com.woolog.repository.MemberRepository;
import com.woolog.request.Signup;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void singup(Signup signup) {

        if (memberRepository.findMemberByEmail(signup.getEmail()).isEmpty()) {
            Member member = signup.toMember(passwordEncoder);
            memberRepository.save(member);
        } else {
            throw new MemberAlreadyExistsException("email", "이미 존재하는 이메일 입니다.");
        }
    }
}

package com.woolog.service;

import com.woolog.domain.Member;

import com.woolog.exception.DuplicateEmailException;
import com.woolog.exception.DuplicateNickNameException;
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
            if (memberRepository.findByNickName(signup.getNickName()).isEmpty()) {
                Member member = signup.toMember(passwordEncoder);
                memberRepository.save(member);
            } else {
                throw new DuplicateNickNameException("nickname", "이미 존재하는 닉네임입니다.");
            }
        } else {
            throw new DuplicateEmailException("email", "이미 존재하는 이메일입니다.");
        }
    }
}

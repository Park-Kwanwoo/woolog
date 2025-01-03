package com.woolog.service;

import com.woolog.domain.Member;
import com.woolog.domain.MemberEditor;
import com.woolog.exception.DuplicateEmailException;
import com.woolog.exception.DuplicateNickNameException;
import com.woolog.exception.MemberNotExist;
import com.woolog.repository.MemberRepository;
import com.woolog.request.MemberEdit;
import com.woolog.request.Signup;
import com.woolog.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void singup(Signup signup) {

        if (memberRepository.findByEmail(signup.getEmail()).isEmpty()) {
            if (memberRepository.findByNickname(signup.getNickname()).isEmpty()) {
                Member member = signup.toMember(passwordEncoder);
                memberRepository.save(member);
            } else {
                throw new DuplicateNickNameException("nickname", "이미 존재하는 닉네임입니다.");
            }
        } else {
            throw new DuplicateEmailException("email", "이미 존재하는 이메일입니다.");
        }
    }

    public MemberResponse getMember(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 사용자입니다."));

        return MemberResponse.of(member);
    }

    @Transactional
    public void editMemberInfo(String email, MemberEdit memberEdit) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 사용자입니다."));

        MemberEditor.MemberEditorBuilder editorBuilder = member.toEditor();

        MemberEditor memberEditor = editorBuilder
                .nickname(memberEdit.getNickname())
                .password(passwordEncoder.encode(memberEdit.getPassword()))
                .build();

        member.edit(memberEditor);
    }

    public void deleteMember(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 사용자입니다."));

        memberRepository.delete(member);
    }
}

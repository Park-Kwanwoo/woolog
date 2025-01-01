package com.woolog.service;

import com.woolog.config.HashEncrypt;
import com.woolog.domain.Member;

import com.woolog.domain.MemberEditor;
import com.woolog.exception.DuplicateEmailException;
import com.woolog.exception.DuplicateNickNameException;
import com.woolog.exception.MemberInfoNotValidException;
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
    private final HashEncrypt hashEncrypt;

    public void singup(Signup signup) {

        if (memberRepository.findByEmail(signup.getEmail()).isEmpty()) {
            if (memberRepository.findByNickName(signup.getNickName()).isEmpty()) {
                Member member = signup.toMember(passwordEncoder, hashEncrypt);
                log.info("{}", member.getHashId());
                memberRepository.save(member);
            } else {
                throw new DuplicateNickNameException("nickname", "이미 존재하는 닉네임입니다.");
            }
        } else {
            throw new DuplicateEmailException("email", "이미 존재하는 이메일입니다.");
        }
    }

    public MemberResponse getMember(String memberHashId, String email) {

        Member member = memberRepository.findByHashId(memberHashId)
                .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 사용자입니다."));

        if (!member.getEmail().equals(email)) {
            throw new MemberInfoNotValidException();
        }

        return MemberResponse.of(member);
    }

    @Transactional
    public void editMemberInfo(String memberHashId, MemberEdit memberEdit) {

        Member member = memberRepository.findByHashId(memberHashId)
                .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 사용자입니다."));

        MemberEditor.MemberEditorBuilder editorBuilder = member.toEditor();

        MemberEditor memberEditor = editorBuilder
                .nickName(memberEdit.getNickName())
                .password(passwordEncoder.encode(memberEdit.getPassword()))
                .build();

        member.edit(memberEditor);
    }

    public void deleteMember(String memberHashId) {

        Member member = memberRepository.findByHashId(memberHashId)
                .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 사용자입니다."));

        memberRepository.delete(member);
    }
}

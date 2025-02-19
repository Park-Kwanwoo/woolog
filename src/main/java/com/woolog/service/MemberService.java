package com.woolog.service;

import com.woolog.domain.Member;
import com.woolog.domain.NicknameEditor;
import com.woolog.domain.PasswordEditor;
import com.woolog.exception.DuplicateEmailException;
import com.woolog.exception.DuplicateNickNameException;
import com.woolog.exception.InvalidPassword;
import com.woolog.exception.MemberNotExist;
import com.woolog.repository.member.MemberRepository;
import com.woolog.request.member.NicknameCheck;
import com.woolog.request.member.EmailCheck;
import com.woolog.request.member.NicknameEdit;
import com.woolog.request.member.PasswordEdit;
import com.woolog.request.member.Signup;
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

    public void signup(Signup signup) {

        if (!memberRepository.existsByEmail(signup.getEmail())) {
            if (!memberRepository.existsByNickname(signup.getNickname())) {
                Member member = signup.toMember(passwordEncoder);
                memberRepository.save(member);
            } else {
                throw new DuplicateNickNameException();
            }
        } else {
            throw new DuplicateEmailException();
        }
    }

    public MemberResponse getMember(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotExist::new);

        return MemberResponse.of(member);
    }

    @Transactional
    public MemberResponse editNickname(String email, NicknameEdit nicknameEdit) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotExist::new);

        if (memberRepository.existsByNickname(nicknameEdit.getNickname()) &&
                !member.getNickname().equals(nicknameEdit.getNickname())) {
            throw new DuplicateNickNameException();
        }

        NicknameEditor.NicknameEditorBuilder editorBuilder = member.toNicknameEditor();

        NicknameEditor nicknameEditor = editorBuilder
                .nickname(nicknameEdit.getNickname())
                .build();

        member.editNickname(nicknameEditor);

        return MemberResponse.of(member);
    }

    @Transactional
    public MemberResponse editPassword(String email, PasswordEdit passwordEdit) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotExist::new);

        if (passwordEdit.getPassword() == null || passwordEdit.getPassword().isEmpty()) {
            throw new InvalidPassword();
        }

        PasswordEditor.PasswordEditorBuilder editorBuilder = member.toPasswordEditor();

        PasswordEditor passwordEditor = editorBuilder
                .password(passwordEncoder.encode(passwordEdit.getPassword()))
                .build();

        member.editPassword(passwordEditor);

        return MemberResponse.of(member);
    }

    public void deleteMember(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotExist::new);

        memberRepository.delete(member);
    }

    public boolean emailDuplicateCheck(EmailCheck emailCheck) {
        return memberRepository.existsByEmail(emailCheck.getEmail());
    }

    public boolean nicknameDuplicateCheck(NicknameCheck nicknameCheck) {
        return memberRepository.existsByNickname(nicknameCheck.getNickname());
    }
}

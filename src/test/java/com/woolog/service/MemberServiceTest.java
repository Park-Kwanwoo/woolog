package com.woolog.service;

import com.woolog.domain.Member;
import com.woolog.domain.Role;
import com.woolog.exception.DuplicateEmailException;
import com.woolog.exception.DuplicateNickNameException;
import com.woolog.exception.MemberNotExist;
import com.woolog.repository.MemberRepository;
import com.woolog.request.MemberEdit;
import com.woolog.request.Signup;
import com.woolog.response.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {


    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Signup signupRequest() {
        return Signup.builder()
                .email("member@blog.com")
                .password("qwer123$")
                .name("테스트박")
                .nickName("테스터닉")
                .build();
    }

    @Nested
    @DisplayName("성공 케이스")
    public class success {

        @Test
        @DisplayName("회원가입")
        void SING_UP() {

            // given
            when(memberRepository.save(any(Member.class))).thenReturn(null);

            // when
            memberService.singup(signupRequest());

            // then
            verify(memberRepository, times(1)).save(any(Member.class));
            verify(memberRepository, times(1)).findByEmail(anyString());
            verify(memberRepository, times(1)).findByNickName(anyString());
        }


        @Test
        @DisplayName("회원 정보 가져오기")
        void GET_MEMBER_INFORMATION() {

            // given
            String fakeHashId = anyString();
            Member fakeMember = Member.builder()
                    .email("test@blog.com")
                    .password(passwordEncoder.encode("qwer123$"))
                    .name("페이크박")
                    .nickName("페이커")
                    .hashId(fakeHashId)
                    .role(Role.MEMBER)
                    .build();

            when(memberRepository.findByHashId(anyString())).thenReturn(Optional.ofNullable(fakeMember));

            // when
            MemberResponse memberResponse = memberService.getMember(fakeHashId, fakeMember.getEmail());

            // then
            assertEquals(memberResponse.getEmail(), "test@blog.com");
            assertEquals(memberResponse.getName(), "페이크박");
            assertEquals(memberResponse.getNickName(), "페이커");

            verify(memberRepository, times(1)).findByHashId(fakeHashId);

        }

        @Test
        @DisplayName("회원정보 수정")
        void EDIT_MEMBER_INFORMATION() {

            // given
            String fakeHashId = "fakeHashValue";
            Member fakeMember = Member.builder()
                    .email("test@blog.com")
                    .password(passwordEncoder.encode("qwer123$"))
                    .name("페이크박")
                    .nickName("페이커")
                    .hashId(fakeHashId)
                    .role(Role.MEMBER)
                    .build();

            MemberEdit memberEdit = MemberEdit.builder()
                    .nickName("페이작아")
                    .password("qwer123$")
                    .build();

            when(memberRepository.findByHashId(fakeHashId)).thenReturn(Optional.of(fakeMember));

            // when
            memberService.editMemberInfo(fakeHashId, memberEdit);
            MemberResponse updatedMember = memberService.getMember(fakeHashId, fakeMember.getEmail());

            // then
//            assertEquals(memberEdit.getNickName(), updatedMember.getNickName());
            verify(memberRepository, times(2)).findByHashId(fakeHashId);
        }

        @Test
        @DisplayName("회원정보 삭제")
        void DELETE_MEMBER_INFORMATION() {

            // given
            String fakeHashId = "fakeHashValue";
            Member fakeMember = Member.builder()
                    .email("test@blog.com")
                    .password(passwordEncoder.encode("qwer123$"))
                    .name("페이크박")
                    .nickName("페이커")
                    .hashId(fakeHashId)
                    .role(Role.MEMBER)
                    .build();

            when(memberRepository.findByHashId(fakeHashId))
                    .thenReturn(Optional.of(fakeMember))
                    .thenThrow(MemberNotExist.class);

            // when
            memberService.deleteMember(fakeHashId);

            // then
            assertThrows(MemberNotExist.class, () -> memberService.deleteMember(fakeHashId));
            verify(memberRepository, times(2)).findByHashId(fakeHashId);
            verify(memberRepository, times(1)).delete(fakeMember);
        }
    }


    @Nested
    @DisplayName("실패 케이스")
    public class failure {

        @Test
        @DisplayName("회원가입 - 중복 이메일")
        void SING_UP_THROW_DUPLICATE_EMAIL() {

            // given
            doThrow(new DuplicateEmailException("email","이미 존재하는 이메일입니다.")).when(memberRepository).save(any(Member.class));

            // expected
            DuplicateEmailException e = assertThrows(DuplicateEmailException.class, () -> memberService.singup(signupRequest()));
            assertEquals("email", e.getErrorResponse().getField());
            assertEquals("이미 존재하는 이메일입니다.", e.getErrorResponse().getMessage());
            verify(memberRepository, times(1)).save(any(Member.class));
            verify(memberRepository, times(1)).findByEmail(anyString());
            verify(memberRepository, times(1)).findByNickName(anyString());
        }

        @Test
        @DisplayName("회원가입 - 중복 닉네임")
        void SING_UP_THROW_DUPLICATE_NICKNAME() {

            // given
            doThrow(new DuplicateNickNameException("nickname","이미 존재하는 닉네임입니다.")).when(memberRepository).save(any(Member.class));

            // expected
            DuplicateNickNameException e = assertThrows(DuplicateNickNameException.class, () -> memberService.singup(signupRequest()));
            assertEquals("nickname", e.getErrorResponse().getField());
            assertEquals("이미 존재하는 닉네임입니다.", e.getErrorResponse().getMessage());
            verify(memberRepository, times(1)).save(any(Member.class));
            verify(memberRepository, times(1)).findByEmail(anyString());
            verify(memberRepository, times(1)).findByNickName(anyString());
        }

        @Test
        @DisplayName("회원정보 수정 - 존재하지 않는 회원")
        void EDIT_MEMBER_INFORMATION() {

            // given
            doThrow(new MemberNotExist("member","존재하지 않는 회원입니다.")).when(memberRepository).findByHashId(anyString());

            // expected
            MemberEdit memberEdit = MemberEdit.builder().build();
            MemberNotExist e = assertThrows(MemberNotExist.class, () -> memberService.editMemberInfo(anyString(), memberEdit));
            assertEquals("member", e.getErrorResponse().getField());
            assertEquals("존재하지 않는 회원입니다.", e.getErrorResponse().getMessage());
            verify(memberRepository, times(1)).findByHashId(anyString());
        }

        @Test
        @DisplayName("회원정보 삭제 - 존재하지 않는 회원")
        void DELETE_MEMBER_INFORMATION() {

            // given
            doThrow(new MemberNotExist("member","존재하지 않는 회원입니다.")).when(memberRepository).findByHashId(anyString());

            // expected
            MemberNotExist e = assertThrows(MemberNotExist.class, () -> memberService.deleteMember(anyString()));
            assertEquals("member", e.getErrorResponse().getField());
            assertEquals("존재하지 않는 회원입니다.", e.getErrorResponse().getMessage());
            verify(memberRepository, times(1)).findByHashId(anyString());
        }
    }
}
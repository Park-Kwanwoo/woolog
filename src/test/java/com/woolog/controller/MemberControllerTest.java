package com.woolog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.config.HashEncrypt;
import com.woolog.config.JwtTokenGenerator;
import com.woolog.domain.Member;
import com.woolog.domain.Role;
import com.woolog.exception.DuplicateEmailException;
import com.woolog.exception.DuplicateNickNameException;
import com.woolog.exception.MemberNotExist;
import com.woolog.repository.MemberRepository;
import com.woolog.request.MemberEdit;
import com.woolog.request.Signup;
import com.woolog.response.ResponseStatus;
import com.woolog.service.MemberService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private HashEncrypt hashEncrypt;

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    private Member memberCreate() {
        Member member = Member.builder()
                .email("member@blog.com")
                .name("선점박")
                .password("qwer123$")
                .nickName("선점박")
                .hashId(hashEncrypt.encrypt("member@blog.com"))
                .role(Role.MEMBER)
                .build();

        return memberRepository.save(member);
    }

    @Nested
    @DisplayName("성공 케이스")
    class successCase {

        @Test
        @DisplayName("회원가입 성공")
        void SUCCESS_SING_UP() throws Exception {

            // given
            Signup signup = Signup.builder()
                    .email("member@blog.com")
                    .password("qwer123$")
                    .name("테스터박")
                    .nickName("테스터박")
                    .build();

            String json = objectMapper.writeValueAsString(signup);

            // expected
            mockMvc.perform(post("/members/signup")
                            .contentType(APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isOk())
                    .andDo(print());

        }

        @Test
        @DisplayName("회원정보 가져오기")
        void GET_MEMBER_INFO() throws Exception {

            // given
            Member member = memberCreate();
            String memberHashId = member.getHashId();

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            // expected
            mockMvc.perform(get("/members/{memberHashId}", memberHashId)
                            .contentType(APPLICATION_JSON)
                            .headers(headers)
                            .cookie(cookie))
                    .andExpect(jsonPath("$.email").value(member.getEmail()))
                    .andExpect(jsonPath("$.name").value(member.getName()))
                    .andExpect(jsonPath("$.nickName").value(member.getNickName()))
                    .andDo(print());

        }

        @Test
        @DisplayName("회원정보 수정")
        void EDIT_MEMBER_INFO() throws Exception {

            // given
            Member member = memberCreate();
            String memberHashId = member.getHashId();

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            MemberEdit memberEdit = MemberEdit.builder()
                    .nickName("닉네임 변경")
                    .password("qwer123$")
                    .build();

            String editRequest = objectMapper.writeValueAsString(memberEdit);

            // expected
            mockMvc.perform(patch("/members/{memberHashId}", memberHashId)
                            .contentType(APPLICATION_JSON)
                            .content(editRequest)
                            .headers(headers)
                            .cookie(cookie))
                    .andDo(print());

            Member editMember = memberRepository.findMemberByEmail(member.getEmail())
                    .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 이메일입니다."));

            assertEquals("닉네임 변경", editMember.getNickName());

        }

        @Test
        @DisplayName("회원탈퇴")
        void DELETE_MEMBER_INFO() throws Exception {

            // given
            Member member = memberCreate();
            String memberHashId = member.getHashId();

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            // expected
            mockMvc.perform(delete("/members/{memberHashId}", memberHashId)
                            .contentType(APPLICATION_JSON)
                            .headers(headers)
                            .cookie(cookie))
                    .andDo(print());

            assertThrows(MemberNotExist.class, () -> memberService.getMember(memberHashId));
        }
    }

    @Nested
    @DisplayName("실패 케이스")
    class failureCase {

        @Test
        @DisplayName("회원가입 실패 - 변수 값 NULL")
        void FAIL_SING_UP_FIELD_VALIDATION() throws Exception {

            // given
            Signup signup = Signup.builder().build();
            String json = objectMapper.writeValueAsString(signup);

            // expected
            mockMvc.perform(post("/members/signup")
                            .content(json)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(ResponseStatus.BAD_REQUEST.getStatus()))
                    .andExpect(jsonPath("$.message").value(ResponseStatus.BAD_REQUEST.getMessage()))
                    .andExpect(result ->
                            assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                    .andDo(print());
        }

        @Test
        @DisplayName("회원가입 실패 - 중복된 이메일")
        void FAIL_SING_UP_DUPLICATE_EMAIL() throws Exception {

            // given
            Member member = Member.builder()
                    .email("member@blog.com")
                    .name("선점박")
                    .password("qwer123$")
                    .nickName("선점박")
                    .hashId(hashEncrypt.encrypt("member@blog.com"))
                    .role(Role.MEMBER)
                    .build();
            memberRepository.save(member);

            Signup signup = Signup.builder()
                    .email("member@blog.com")
                    .name("테스터박")
                    .password("qwer123$")
                    .nickName("테스터박")
                    .build();

            String json = objectMapper.writeValueAsString(signup);

            // expected
            mockMvc.perform(post("/members/signup")
                            .content(json)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(ResponseStatus.DUPLICATE_EMAIL_EXCEPTION.getStatus()))
                    .andExpect(jsonPath("$.message").value(ResponseStatus.DUPLICATE_EMAIL_EXCEPTION.getMessage()))
                    .andExpect(jsonPath("$.data[0].field").value("email"))
                    .andExpect(jsonPath("$.data[0].message").value("이미 존재하는 이메일입니다."))
                    .andExpect(result ->
                            assertInstanceOf(DuplicateEmailException.class, result.getResolvedException()))
                    .andDo(print());
        }

        @Test
        @DisplayName("회원가입 실패 - 중복된 닉네임")
        void FAIL_SING_UP_DUPLICATE_NICKNAME() throws Exception {

            // given
            Member member = Member.builder()
                    .email("test1@blog.com")
                    .name("선점닉")
                    .password("qwer123$")
                    .nickName("선점닉")
                    .hashId(hashEncrypt.encrypt("test1@blog.com"))
                    .role(Role.MEMBER)
                    .build();
            memberRepository.save(member);

            Signup signup = Signup.builder()
                    .email("test2@blog.com")
                    .name("테스터박")
                    .password("qwer123$")
                    .nickName("선점닉")
                    .build();

            String json = objectMapper.writeValueAsString(signup);

            // expected
            mockMvc.perform(post("/members/signup")
                            .content(json)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(ResponseStatus.DUPLICATE_NICKNAME_EXCEPTION.getStatus()))
                    .andExpect(jsonPath("$.message").value(ResponseStatus.DUPLICATE_NICKNAME_EXCEPTION.getMessage()))
                    .andExpect(jsonPath("$.data[0].field").value("nickname"))
                    .andExpect(jsonPath("$.data[0].message").value("이미 존재하는 닉네임입니다."))
                    .andExpect(result ->
                            assertInstanceOf(DuplicateNickNameException.class, result.getResolvedException()))
                    .andDo(print());
        }
    }
}
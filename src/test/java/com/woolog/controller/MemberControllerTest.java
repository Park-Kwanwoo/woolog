package com.woolog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.domain.Member;
import com.woolog.domain.Role;
import com.woolog.exception.DuplicateEmailException;
import com.woolog.exception.DuplicateNickNameException;
import com.woolog.repository.MemberRepository;
import com.woolog.request.Signup;
import com.woolog.response.ResponseStatus;
import com.woolog.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Nested
    @DisplayName("성공 케이스")
    class successCase {

        @Test
        @DisplayName("회원가입 성공")
        void SUCCESS_SING_UP() throws Exception {

            // given
            Signup signup = Signup.builder()
                    .email("test@blog.com")
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
                    .andExpect(jsonPath("$.code").value(ResponseStatus.BAD_REQUEST.getCode()))
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
                    .email("test@blog.com")
                    .name("선점박")
                    .password("qwer123$")
                    .nickName("선점박")
                    .role(Role.MEMBER)
                    .build();
            memberRepository.save(member);

            Signup signup = Signup.builder()
                    .email("test@blog.com")
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
                    .andExpect(jsonPath("$.code").value(ResponseStatus.DUPLICATE_EMAIL_EXCEPTION.getCode()))
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
                    .andExpect(jsonPath("$.code").value(ResponseStatus.DUPLICATE_NICKNAME_EXCEPTION.getCode()))
                    .andExpect(jsonPath("$.message").value(ResponseStatus.DUPLICATE_NICKNAME_EXCEPTION.getMessage()))
                    .andExpect(jsonPath("$.data[0].field").value("nickname"))
                    .andExpect(jsonPath("$.data[0].message").value("이미 존재하는 닉네임입니다."))
                    .andExpect(result ->
                            assertInstanceOf(DuplicateNickNameException.class, result.getResolvedException()))
                    .andDo(print());
        }
    }
}
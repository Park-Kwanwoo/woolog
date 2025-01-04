package com.woolog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.annotation.CustomWithMockUser;
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
import org.junit.jupiter.api.AfterEach;
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

    @AfterEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("회원 - 성공")
    class success {

        @Test
        @DisplayName("회원가입 성공")
        void SUCCESS_SING_UP() throws Exception {

            // given
            Signup signup = Signup.builder()
                    .email("member@blog.com")
                    .password("qwer123$")
                    .name("테스터박")
                    .nickname("테스터박")
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
        @CustomWithMockUser(email = "member@blog.com", role = "MEMBER")
        @DisplayName("회원정보 가져오기")
        void GET_MEMBER_INFO() throws Exception {

            // given
            Member member = memberRepository.findByEmail("member@blog.com")
                    .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 이메일입니다."));

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            // expected
            mockMvc.perform(get("/members")
                            .contentType(APPLICATION_JSON)
                            .headers(headers)
                            .cookie(cookie))
                    .andExpect(jsonPath("$.email").value(member.getEmail()))
                    .andExpect(jsonPath("$.name").value(member.getName()))
                    .andExpect(jsonPath("$.nickname").value(member.getNickname()))
                    .andDo(print());

        }

        @Test
        @CustomWithMockUser(email = "member@blog.com", role = "MEMBER")
        @DisplayName("회원정보 수정")
        void EDIT_MEMBER_INFO() throws Exception {

            // given
            Member member = memberRepository.findByEmail("member@blog.com")
                    .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 이메일입니다."));

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            MemberEdit memberEdit = MemberEdit.builder()
                    .nickname("닉네임 변경")
                    .password("qwer123$")
                    .build();

            String editRequest = objectMapper.writeValueAsString(memberEdit);

            // expected
            mockMvc.perform(patch("/members")
                            .contentType(APPLICATION_JSON)
                            .content(editRequest)
                            .headers(headers)
                            .cookie(cookie))
                    .andDo(print());

            Member editMember = memberRepository.findByEmail(member.getEmail())
                    .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 이메일입니다."));

            assertEquals("닉네임 변경", editMember.getNickname());

        }

        @Test
        @CustomWithMockUser(email = "member@blog.com", role = "MEMBER")
        @DisplayName("회원탈퇴")
        void DELETE_MEMBER_INFO() throws Exception {

            // given
            Member member = memberRepository.findByEmail("member@blog.com")
                    .orElseThrow(() -> new MemberNotExist("member", "존재하지 않는 이메일입니다."));

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            // expected
            mockMvc.perform(delete("/members")
                            .contentType(APPLICATION_JSON)
                            .headers(headers)
                            .cookie(cookie))
                    .andDo(print());

            assertThrows(MemberNotExist.class, () -> memberService.getMember(member.getEmail()));
        }
    }

    @Nested
    @DisplayName("회원 - 실패")
    class fail {

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
                    .email("member@blog.com")
                    .name("선점박")
                    .password("qwer123$")
                    .nickname("선점박")
                    .role(Role.MEMBER)
                    .build();
            memberRepository.save(member);

            Signup signup = Signup.builder()
                    .email("member@blog.com")
                    .name("테스터박")
                    .password("qwer123$")
                    .nickname("테스터박")
                    .build();

            String json = objectMapper.writeValueAsString(signup);

            // expected
            mockMvc.perform(post("/members/signup")
                            .content(json)
                            .contentType(APPLICATION_JSON))
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
        void FAIL_SING_UP_DUPLICATE_nickname() throws Exception {

            // given
            Member member = Member.builder()
                    .email("test1@blog.com")
                    .name("선점닉")
                    .password("qwer123$")
                    .nickname("선점닉")
                    .role(Role.MEMBER)
                    .build();
            memberRepository.save(member);

            Signup signup = Signup.builder()
                    .email("test2@blog.com")
                    .name("테스터박")
                    .password("qwer123$")
                    .nickname("선점닉")
                    .build();

            String json = objectMapper.writeValueAsString(signup);

            // expected
            mockMvc.perform(post("/members/signup")
                            .content(json)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ResponseStatus.DUPLICATE_NICKNAME_EXCEPTION.getCode()))
                    .andExpect(jsonPath("$.message").value(ResponseStatus.DUPLICATE_NICKNAME_EXCEPTION.getMessage()))
                    .andExpect(jsonPath("$.data[0].field").value("nickname"))
                    .andExpect(jsonPath("$.data[0].message").value("이미 존재하는 닉네임입니다."))
                    .andExpect(result ->
                            assertInstanceOf(DuplicateNickNameException.class, result.getResolvedException()))
                    .andDo(print());
        }

        @Test
        @CustomWithMockUser(email = "member@blog.com", role = "MEMBER")
        @DisplayName("회원정보 수정 실패 - nickname null")
        void FAIL_EDIT_MEMBER_INFO() throws Exception {

            MemberEdit memberEdit = MemberEdit.builder()
                    .password("qwer123$")
                    .build();

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            String request = objectMapper.writeValueAsString(memberEdit);

            mockMvc.perform(patch("/members")
                            .contentType(APPLICATION_JSON)
                            .content(request)
                            .headers(headers)
                            .cookie(cookie))
                    .andExpect(jsonPath("$.code").value(400))
                    .andExpect(jsonPath("$.message").value("잘못된 입력 값입니다."))
                    .andExpect(jsonPath("$.data[0].field").value("nickname"))
                    .andExpect(jsonPath("$.data[0].message").value("닉네임을 입력해주세요."))
                    .andDo(print());

        }
    }
}
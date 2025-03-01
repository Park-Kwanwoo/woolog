package com.woolog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.annotation.CustomWithMockUser;
import com.woolog.config.JwtTokenGenerator;
import com.woolog.domain.Member;
import com.woolog.domain.Role;
import com.woolog.exception.DuplicateEmailException;
import com.woolog.exception.DuplicateNickNameException;
import com.woolog.exception.MemberNotExist;
import com.woolog.repository.member.MemberRepository;
import com.woolog.request.member.NicknameEdit;
import com.woolog.request.member.PasswordEdit;
import com.woolog.request.member.Signup;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    PasswordEncoder passwordEncoder;

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
                    .orElseThrow(MemberNotExist::new);

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
                    .andExpect(jsonPath("$.data.email").value(member.getEmail()))
                    .andExpect(jsonPath("$.data.name").value(member.getName()))
                    .andExpect(jsonPath("$.data.nickname").value(member.getNickname()))
                    .andDo(print());

        }

        @Test
        @CustomWithMockUser(email = "member@blog.com", role = "MEMBER")
        @DisplayName("닉네임 수정")
        void EDIT_MEMBER_NICKNAME() throws Exception {

            // given
            Member member = memberRepository.findByEmail("member@blog.com")
                    .orElseThrow(MemberNotExist::new);

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            NicknameEdit nicknameEdit = NicknameEdit.builder()
                    .nickname("닉네임 변경")
                    .build();

            String editRequest = objectMapper.writeValueAsString(nicknameEdit);

            // expected
            mockMvc.perform(patch("/members/nickname")
                            .contentType(APPLICATION_JSON)
                            .content(editRequest)
                            .headers(headers)
                            .cookie(cookie))
                    .andDo(print());

            Member editMember = memberRepository.findByEmail(member.getEmail())
                    .orElseThrow(MemberNotExist::new);

            assertEquals("닉네임 변경", editMember.getNickname());

        }

        @Test
        @CustomWithMockUser(email = "member@blog.com", role = "MEMBER")
        @DisplayName("비밀번호 수정")
        void EDIT_MEMBER_PASSWORD() throws Exception {

            // given
            Member member = memberRepository.findByEmail("member@blog.com")
                    .orElseThrow(MemberNotExist::new);

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            PasswordEdit passwordEdit = PasswordEdit.builder()
                    .password("1234")
                    .build();

            String editRequest = objectMapper.writeValueAsString(passwordEdit);

            // expected
            mockMvc.perform(patch("/members/password")
                            .contentType(APPLICATION_JSON)
                            .content(editRequest)
                            .headers(headers)
                            .cookie(cookie))
                    .andDo(print());

            Member editMember = memberRepository.findByEmail(member.getEmail())
                    .orElseThrow(MemberNotExist::new);

            assertTrue(passwordEncoder.matches(passwordEdit.getPassword(), editMember.getPassword()));

        }

        @Test
        @CustomWithMockUser(email = "member@blog.com", role = "MEMBER")
        @DisplayName("회원탈퇴")
        void DELETE_MEMBER_INFO() throws Exception {

            // given
            Member member = memberRepository.findByEmail("member@blog.com")
                    .orElseThrow(MemberNotExist::new);

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
                    .andExpect(jsonPath("$.statusCode").value("ERROR"))
                    .andExpect(jsonPath("$.data.length()").value(4))

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
                    .andExpect(jsonPath("$.statusCode").value("ERROR"))
                    .andExpect(jsonPath("$.message").value("중복된 이메일입니다."))
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
                    .andExpect(jsonPath("$.statusCode").value("ERROR"))
                    .andExpect(jsonPath("$.message").value("중복된 닉네임입니다."))
                    .andExpect(result ->
                            assertInstanceOf(DuplicateNickNameException.class, result.getResolvedException()))
                    .andDo(print());
        }

        @Test
        @CustomWithMockUser(email = "member@blog.com", role = "MEMBER")
        @DisplayName("닉네임 수정 실패")
        void FAIL_EDIT_MEMBER_NICKNAME() throws Exception {

            NicknameEdit nicknameEdit = NicknameEdit.builder()
                    .build();

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            String request = objectMapper.writeValueAsString(nicknameEdit);

            mockMvc.perform(patch("/members/nickname")
                            .contentType(APPLICATION_JSON)
                            .content(request)
                            .headers(headers)
                            .cookie(cookie))
                    .andExpect(jsonPath("$.statusCode").value("ERROR"))
                    .andExpect(jsonPath("$.data[0].field").value("nickname"))
                    .andExpect(jsonPath("$.data[0].message").value("닉네임을 입력해주세요."))
                    .andDo(print());

        }

        @Test
        @CustomWithMockUser(email = "member@blog.com", role = "MEMBER")
        @DisplayName("비밀번호 수정 실패")
        void FAIL_EDIT_MEMBER_PASSWORD() throws Exception {

            PasswordEdit passwordEdit = PasswordEdit.builder()
                    .build();

            String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            String request = objectMapper.writeValueAsString(passwordEdit);

            mockMvc.perform(patch("/members/password")
                            .contentType(APPLICATION_JSON)
                            .content(request)
                            .headers(headers)
                            .cookie(cookie))
                    .andExpect(jsonPath("$.statusCode").value("ERROR"))
                    .andExpect(jsonPath("$.data[0].field").value("password"))
                    .andExpect(jsonPath("$.data[0].message").value("비밀번호를 입력해주세요."))
                    .andDo(print());

        }
    }
}
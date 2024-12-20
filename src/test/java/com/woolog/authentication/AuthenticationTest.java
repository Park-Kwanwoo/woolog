package com.woolog.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.config.JwtTokenGenerator;
import com.woolog.domain.Member;
import com.woolog.domain.Role;
import com.woolog.repository.MemberRepository;
import com.woolog.request.Login;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.woolog.response.ResponseStatus.AUTHORIZE_EXCEPTION;
import static com.woolog.response.ResponseStatus.MEMBER_AUTHENTICATION_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
public class AuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    private void createAdmin() {

        Member admin = Member.builder()
                .email("admin@blog.com")
                .password(passwordEncoder.encode("admin123$"))
                .name("관리자")
                .nickName("관리자박")
                .role(Role.ADMIN)
                .build();

        memberRepository.save(admin);
    }

    private void createMember() {

        Member member = Member.builder()
                .email("member@blog.com")
                .password(passwordEncoder.encode("member123$"))
                .name("멤버스")
                .nickName("멤버박")
                .role(Role.MEMBER)
                .build();

        memberRepository.save(member);
    }

    @BeforeEach
    void setUp() {
        createMember();
        createAdmin();
    }
    @AfterEach
    void clean() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 요청 성공 시 JWT 토큰 발급 후 RESPONSE")
    void GENERATE_JWT_TOKEN_WHEN_LOGIN() throws Exception {

        // given
        Login login = Login.builder()
                .email("member@blog.com")
                .password("member123$")
                .build();

        String loginRequest = objectMapper.writeValueAsString(login);

        // expected
        this.mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(status().isFound())
                .andExpect(result -> {
                    MockHttpServletResponse response = result.getResponse();

                    Cookie[] cookies = response.getCookies();
                    for (Cookie cookie : cookies) {
                        assertEquals(60 * 60 * 24, cookie.getMaxAge());
                        assertEquals("true", cookie.getAttribute("httpOnly"));
//                        assertTrue(jwtTokenGenerator.verifyRefreshToken(cookie.getValue()));

                    }
                    assertTrue(jwtTokenGenerator.validateToken(response.getHeader("Authorization")));
                })
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 정보로 로그인 시 MemberNotExist 예외 발생")
    void THROW_MEMBER_NOT_EXIST_WHEN_LOGIN() throws Exception {

        // given
        Login login = Login.builder()
                .email("wrong@blog.com")
                .password("qwer123$")
                .build();

        String loginRequest = objectMapper.writeValueAsString(login);

        // expected
        this.mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.status").value(MEMBER_AUTHENTICATION_EXCEPTION.getStatus()))
                .andExpect(jsonPath("$.code").value(MEMBER_AUTHENTICATION_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.message").value(MEMBER_AUTHENTICATION_EXCEPTION.getMessage()))
                .andExpect(jsonPath("$.data[0].field").value("MEMBER"))
                .andExpect(jsonPath("$.data[0].message").value("존재하지 않는 사용자입니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("비밀번호 불일치 시 MemberAuthentication 예외 발생")
    void THROW_MEMBER_AUTHENTICATION_EXCEPTION_WHEN_LOGIN() throws Exception {

        // given
        Login login = Login.builder()
                .email("member@blog.com")
                .password("wrong123$")
                .build();

        String loginRequest = objectMapper.writeValueAsString(login);

        // expected
        this.mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest)
                )
                .andExpect(jsonPath("$.status").value(MEMBER_AUTHENTICATION_EXCEPTION.getStatus()))
                .andExpect(jsonPath("$.code").value(MEMBER_AUTHENTICATION_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.message").value(MEMBER_AUTHENTICATION_EXCEPTION.getMessage()))
                .andExpect(jsonPath("$.data[0].field").value("MEMBER"))
                .andExpect(jsonPath("$.data[0].message").value("아이디나 비밀번호가 잘못되었습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("접근 권한이 없는 resources 접근 시 exception 발생")
    void THROW_AUTHORIZE_EXCEPTION_HAVE_NOT_AUTHORITY() throws Exception {

        // given
        Login login = Login.builder()
                .email("member@blog.com")
                .password("member123$")
                .build();

        String loginRequest = objectMapper.writeValueAsString(login);

        String accessToken = jwtTokenGenerator.generateAccessToken("member@blog.com");
        String refreshToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        Cookie cookie = new Cookie("refreshToken", refreshToken);

        // expected
        this.mockMvc.perform(get("/admin")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest)
                        .headers(headers)
                        .cookie(cookie)
                )
                .andExpect(jsonPath("$.status").value(AUTHORIZE_EXCEPTION.getStatus()))
                .andExpect(jsonPath("$.code").value(AUTHORIZE_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.message").value(AUTHORIZE_EXCEPTION.getMessage()))
                .andDo(print());

    }
}

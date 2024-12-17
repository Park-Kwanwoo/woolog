package com.woolog.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.config.JwtTokenGenerator;
import com.woolog.domain.Member;
import com.woolog.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static com.woolog.response.ResponseStatus.MEMBER_AUTHENTICATION_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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

    public static class Login {

        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public void createTestMember() {

        Member testMember = Member.builder()
                .email("test@blog.com")
                .password(passwordEncoder.encode("qwer123$"))
                .name("아무개")
                .build();

        memberRepository.save(testMember);
    }

    @BeforeEach
    void setUp() {
        createTestMember();
    }

    @AfterEach
    void clean() {
        memberRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 요청 성공 시 JWT 토큰 발급 후 RESPONSE")
    void GENERATE_JWT_TOKEN_WHEN_LOGIN() throws Exception {

        // given
        Login login = new Login();
        login.setEmail("test@blog.com");
        login.setPassword("qwer123$");
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
                        assertTrue(jwtTokenGenerator.verifyRefreshToken(cookie.getValue()));

                    }
                    assertTrue(jwtTokenGenerator.verifyAccessToken(response.getHeader("Authorization")));
                })
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 정보로 로그인 시 MemberNotExist 예외 발생")
    void THROW_MEMBER_NOT_EXIST_WHEN_LOGIN() throws Exception {

        // given
        Login login = new Login();
        login.setEmail("notexist@blog.com");
        login.setPassword("password");
        String loginRequest = objectMapper.writeValueAsString(login);

        // expected
        this.mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.status").value(MEMBER_AUTHENTICATION_EXCEPTION.getStatus()))
                .andExpect(jsonPath("$.code").value(MEMBER_AUTHENTICATION_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.description").value(MEMBER_AUTHENTICATION_EXCEPTION.getDescription()))
                .andExpect(jsonPath("$.data[0].field").value("MEMBER"))
                .andExpect(jsonPath("$.data[0].message").value("존재하지 않는 사용자입니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("비밀번호 불일치 시 MemberAuthentication 예외 발생")
    void THROW_MEMBER_AUTHENTICATION_EXCEPTION_WHEN_LOGIN() throws Exception {

        // given
        Login login = new Login();
        login.setEmail("test@blog.com");
        login.setPassword("wrong");
        String loginRequest = objectMapper.writeValueAsString(login);

        // expected
        this.mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andExpect(jsonPath("$.status").value(MEMBER_AUTHENTICATION_EXCEPTION.getStatus()))
                .andExpect(jsonPath("$.code").value(MEMBER_AUTHENTICATION_EXCEPTION.getCode()))
                .andExpect(jsonPath("$.description").value(MEMBER_AUTHENTICATION_EXCEPTION.getDescription()))
                .andExpect(jsonPath("$.data[0].field").value("MEMBER"))
                .andExpect(jsonPath("$.data[0].message").value("아이디나 비밀번호가 잘못되었습니다."))
                .andDo(print());

    }
}

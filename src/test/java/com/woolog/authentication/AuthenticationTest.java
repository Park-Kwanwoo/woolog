package com.woolog.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.annotation.CustomWithMockUser;
import com.woolog.config.HashEncrypt;
import com.woolog.config.JwtTokenGenerator;
import com.woolog.domain.Member;
import com.woolog.domain.Role;
import com.woolog.repository.MemberRepository;
import com.woolog.request.Login;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import org.aspectj.util.Reflection;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.woolog.response.ResponseStatus.AUTHORIZE_EXCEPTION;
import static com.woolog.response.ResponseStatus.MEMBER_AUTHENTICATION_EXCEPTION;
import static org.junit.jupiter.api.Assertions.*;
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
    private JwtTokenGenerator jwtTokenGenerator;

    @AfterEach
    void clean() {
        memberRepository.deleteAll();
    }

    @Nested
    @DisplayName("인증 - 성공")
    public class success {
        @Test
        @CustomWithMockUser(email = "member@blog.com", password = "member123$")
        @DisplayName("로그인 요청 성공 시 JWT 토큰 발급 후 RESPONSE")
        void GENERATE_JWT_TOKEN_WHEN_LOGIN() throws Exception {

            // given
            Login login = Login.builder()
                    .email("member@blog.com")
                    .password("member123$")
                    .build();

            String loginRequest = objectMapper.writeValueAsString(login);
            // expected
            mockMvc.perform(post("/auth/login")
                            .contentType(APPLICATION_JSON)
                            .content(loginRequest))
                    .andExpect(result -> {
                        MockHttpServletResponse response = result.getResponse();

                        Cookie[] cookies = response.getCookies();
                        for (Cookie cookie : cookies) {
                            assertEquals(60 * 60 * 24, cookie.getMaxAge());
                            assertEquals("true", cookie.getAttribute("httpOnly"));

                            if (cookie.getName().equals("refreshToken")) {
                                String refreshToken = cookie.getValue();
                                Claims refresh = jwtTokenGenerator.getPayload(refreshToken);
                                assertEquals(refresh.getSubject(), login.getEmail());
                                break;
                            }
                        }

                        String accessToken = response.getHeader("Authorization");
                        Claims access = jwtTokenGenerator.getPayload(accessToken);
                        assertEquals(access.getSubject(), login.getEmail());
                    })
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("인증 - 실패")
    public class fail {

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
            mockMvc.perform(post("/auth/login")
                            .contentType(APPLICATION_JSON)
                            .content(loginRequest))
                    .andExpect(jsonPath("$.status").value(MEMBER_AUTHENTICATION_EXCEPTION.getStatus()))
                    .andExpect(jsonPath("$.message").value(MEMBER_AUTHENTICATION_EXCEPTION.getMessage()))
                    .andExpect(jsonPath("$.data[0].field").value("MEMBER"))
                    .andExpect(jsonPath("$.data[0].message").value("존재하지 않는 사용자입니다."))
                    .andDo(print());

        }

        @Test
        @CustomWithMockUser(email = "member@blog.com")
        @DisplayName("비밀번호 불일치 시 MemberAuthentication 예외 발생")
        void THROW_MEMBER_AUTHENTICATION_EXCEPTION_WHEN_LOGIN() throws Exception {

            // given
            Login login = Login.builder()
                    .email("member@blog.com")
                    .password("wrong123$")
                    .build();

            String loginRequest = objectMapper.writeValueAsString(login);

            // expected
            mockMvc.perform(post("/auth/login")
                            .contentType(APPLICATION_JSON)
                            .content(loginRequest)
                    )
                    .andExpect(jsonPath("$.status").value(MEMBER_AUTHENTICATION_EXCEPTION.getStatus()))
                    .andExpect(jsonPath("$.message").value(MEMBER_AUTHENTICATION_EXCEPTION.getMessage()))
                    .andExpect(jsonPath("$.data[0].field").value("MEMBER"))
                    .andExpect(jsonPath("$.data[0].message").value("아이디나 비밀번호가 잘못되었습니다."))
                    .andDo(print());

        }

        @Test
        @CustomWithMockUser(email = "member@blog.com", role = "MEMBER")
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
            mockMvc.perform(get("/admin")
                            .contentType(APPLICATION_JSON)
                            .content(loginRequest)
                            .headers(headers)
                            .cookie(cookie)
                    )
                    .andExpect(jsonPath("$.status").value(AUTHORIZE_EXCEPTION.getStatus()))
                    .andExpect(jsonPath("$.message").value(AUTHORIZE_EXCEPTION.getMessage()))
                    .andDo(print());

        }

        @Test
        @CustomWithMockUser
        @DisplayName("유효하지 않은 TOKEN - 기한 만료")
        void EXPIRED_JWT_TOKEN() throws Exception {

            String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBibG9nLmNvbSIsImlzcyI6InRlc3Qtd29vbG9nIiwiaWF0IjoxNzM1Nzk0MjcwLCJleHAiOjE3MzU3OTc4NzB9.WFC8ZuBqKheyt0XfX4p4sfuo77svfEK65cxN_PS8z-4";
            String refreshToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBibG9nLmNvbSIsImlzcyI6InRlc3Qtd29vbG9nIiwiaWF0IjoxNzM1Nzk0MjcwLCJleHAiOjE3MzU4ODA2NzB9.u-gHNVhqgoEmME3H3YNJFSnE4OUNK3WIFf9tMDAmYyY";
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            mockMvc.perform(get("/admin")
                            .contentType(APPLICATION_JSON)
                            .headers(headers)
                            .cookie(cookie))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."))
                    .andDo(print());
        }

        @Test
        @CustomWithMockUser
        @DisplayName("유효하지 않은 TOKEN - JWT 타입 검증")
        void INVALID_JWT_TOKEN_TYPE() throws Exception {

            String accessToken = "zzzz";
            String refreshToken = jwtTokenGenerator.generateRefreshToken("admin@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            mockMvc.perform(get("/admin")
                            .contentType(APPLICATION_JSON)
                            .headers(headers)
                            .cookie(cookie))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."))
                    .andDo(print());
        }

        @Test
        @CustomWithMockUser
        @DisplayName("유효하지 않은 TOKEN - Subject 위조 검증")
        void INVALID_JWT_TOKEN_SUBJECT() throws Exception {

            String accessToken = jwtTokenGenerator.generateRefreshToken("member@blog.com");;
            String refreshToken = jwtTokenGenerator.generateRefreshToken("admin@blog.com");
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            mockMvc.perform(get("/admin")
                            .contentType(APPLICATION_JSON)
                            .headers(headers)
                            .cookie(cookie))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."))
                    .andDo(print());
        }

        @Test
        @CustomWithMockUser
        @DisplayName("유효하지 않은 TOKEN - Signature 검증")
        void INVALID_JWT_TOKEN_Signature() throws Exception {

            JwtTokenGenerator fakeTokenGenerator = new JwtTokenGenerator();
            String fakeSignature = "This is a fakeSignature for test code";
            byte[] bytes = fakeSignature.getBytes(StandardCharsets.UTF_8);
            String fakeSecretKey = Base64.getEncoder().encodeToString(bytes);

            ReflectionTestUtils.setField(fakeTokenGenerator, "secretKey", fakeSecretKey);
            ReflectionTestUtils.setField(fakeTokenGenerator, "accessExpirationTime", 3600L);
            ReflectionTestUtils.setField(fakeTokenGenerator, "refreshExpirationTime", 86400L);
            ReflectionTestUtils.setField(fakeTokenGenerator, "issuer", "woolog");

            String accessToken = fakeTokenGenerator.generateRefreshToken("admin@blog.com");
            String refreshToken = fakeTokenGenerator.generateRefreshToken("admin@blog.com");

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            mockMvc.perform(get("/admin")
                            .contentType(APPLICATION_JSON)
                            .headers(headers)
                            .cookie(cookie))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."))
                    .andDo(print());
        }

        @Test
        @CustomWithMockUser
        @DisplayName("유효하지 않은 TOKEN - Issuer 검증")
        void INVALID_JWT_TOKEN_Issuer() throws Exception {

            String accessToken = jwtTokenGenerator.generateRefreshToken("admin@blog.com");
            String refreshToken = jwtTokenGenerator.generateRefreshToken("admin@blog.com");

            Field field = jwtTokenGenerator.getClass().getDeclaredField("issuer");
            field.setAccessible(true);
            field.set(jwtTokenGenerator, "test-issuer");

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", accessToken);
            Cookie cookie = new Cookie("refreshToken", refreshToken);

            mockMvc.perform(get("/admin")
                            .contentType(APPLICATION_JSON)
                            .headers(headers)
                            .cookie(cookie))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("유효하지 않은 토큰입니다."))
                    .andDo(print());
        }
    }
}

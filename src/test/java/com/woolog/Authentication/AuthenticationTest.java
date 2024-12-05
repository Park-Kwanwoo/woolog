package com.woolog.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.domain.Member;
import com.woolog.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
    @DisplayName("RESPONSE HEADER JWT TEST")
    void RESPONSE_HEADER() throws Exception {

        // given
        Login login = new Login();
        login.setEmail("test@blog.com");
        login.setPassword("qwer123$");
        String loginRequest = objectMapper.writeValueAsString(login);

        // expected
        this.mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(loginRequest))
                .andDo(print());
    }
}

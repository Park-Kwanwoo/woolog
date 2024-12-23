package com.woolog.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.config.HashEncrypt;
import com.woolog.config.JwtTokenGenerator;
import com.woolog.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final HashEncrypt hashEncrypt;

    private final ObjectMapper objectMapper;

    public CustomLoginSuccessHandler(JwtTokenGenerator jwtTokenGenerator, HashEncrypt hashEncrypt, ObjectMapper objectMapper) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.hashEncrypt = hashEncrypt;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        String email = (String) authentication.getPrincipal();
        setResponse(email, response);

        setDefaultTargetUrl("/");
        super.onAuthenticationSuccess(request, response, authentication);

    }

    protected void setResponse(String email, HttpServletResponse response) throws IOException {

        String memberHashId = hashEncrypt.encrypt(email);
        String accessToken = jwtTokenGenerator.generateAccessToken(email);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(email);
        String cookie = setCookie(refreshToken);

        response.setCharacterEncoding(UTF_8.name());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setHeader("Authorization", accessToken);
        response.setHeader("Set-Cookie", cookie);

        ApiResponse<Map<String, String>> apiResponse =
                ApiResponse.<Map<String, String>>builder()
                        .status(HttpStatus.OK.value())
                        .build();
        Map<String, String> map = new HashMap<>();
        map.put("memberHashId", memberHashId);
        apiResponse.addData(map);
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));

        log.info("access = {}", accessToken);
        log.info("refresh = {}", refreshToken);
    }

    private String setCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .maxAge(Duration.ofDays(1))
                .sameSite("Strict")
                .build()
                .toString();
    }
}

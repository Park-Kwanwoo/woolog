package com.woolog.security.handler;

import com.woolog.config.JwtTokenGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Duration;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtTokenGenerator jwtTokenGenerator;

    public CustomLoginSuccessHandler(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        String email = (String) authentication.getPrincipal();
        setResponse(email, response);
        response.getWriter().write("");
    }

    protected void setResponse(String email, HttpServletResponse response) {

        String accessToken = jwtTokenGenerator.generateAccessToken(email);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(email);
        String cookie = setCookie(refreshToken);

        response.setCharacterEncoding(UTF_8.name());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setHeader("Authorization", accessToken);
        response.setHeader("Set-Cookie", cookie);

    }

    private String setCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .domain("localhost")
                .maxAge(Duration.ofDays(1))
                .sameSite("None")
                .path("/")
                .secure(true)
                .build()
                .toString();
    }
}

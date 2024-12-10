package com.woolog.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.config.JwtTokenGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;

public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final ObjectMapper objectMapper;

    public CustomLoginSuccessHandler(JwtTokenGenerator jwtTokenGenerator, ObjectMapper objectMapper) {
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        String email = (String) authentication.getPrincipal();
        String accessToken = jwtTokenGenerator.generateAccessToken(email);
        String refreshToken = jwtTokenGenerator.generateRefreshToken(email);

        response.setHeader("Authorization", accessToken);
        response.setCharacterEncoding("utf-8");
        response.setHeader("Set-Cookie", setCookie(refreshToken));

        setDefaultTargetUrl("/posts");
        super.onAuthenticationSuccess(request, response, authentication);

        // TODO
        //  2. Authorized Filter and Verify Jwt Token (entry) and User role set, Test Code -> TuesDay
        //  3. Comment, Vue // Friday
        //  4. After Basic study and resume

    }

    protected String setCookie(String refreshToken) {

        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .maxAge(Duration.ofDays(1))
                .sameSite("Strict")
                .build()
                .toString();
    }
}

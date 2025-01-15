package com.woolog.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.exception.LoginArgumentValidation;
import com.woolog.request.Login;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Slf4j
@Setter
public class JsonLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper;

    private boolean postOnly = true;

    private static final AntPathRequestMatcher LOGIN_REQUEST_MATCHER = new AntPathRequestMatcher("/auth/login", "POST");

    public JsonLoginFilter(ObjectMapper objectMapper) {
        super(LOGIN_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {

        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            Login login = objectMapper.readValue(request.getInputStream(), Login.class);

            if (login.getEmail().isEmpty() || login.getPassword().isEmpty()) {
                throw new LoginArgumentValidation();
            }

            UsernamePasswordAuthenticationToken unauthenticatedLoginInfo = UsernamePasswordAuthenticationToken.unauthenticated(login.getEmail(), login.getPassword());

            unauthenticatedLoginInfo.setDetails(this.authenticationDetailsSource.buildDetails(request));
            return this.getAuthenticationManager().authenticate(unauthenticatedLoginInfo);
        }
    }
}

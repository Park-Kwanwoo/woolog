package com.woolog.security.handler;

import com.woolog.exception.JwtValidException;
import com.woolog.exception.MemberInfoNotValidException;
import com.woolog.exception.MemberNotExist;
import com.woolog.exception.WoologException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomEntryPoint implements AuthenticationEntryPoint {

    private final HandlerExceptionResolver resolver;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        Object ex = request.getAttribute("exception");

        if (ex instanceof JwtException) {
            resolver.resolveException(request, response, null, new JwtValidException());
        } else if (ex instanceof WoologException){
            resolver.resolveException(request, response, null, new MemberInfoNotValidException());
        } else {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
    }
}

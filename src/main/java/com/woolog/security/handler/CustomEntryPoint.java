package com.woolog.security.handler;

import com.woolog.exception.JwtValidException;
import com.woolog.exception.MemberNotExist;
import com.woolog.exception.WoologException;
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

        Object exception = request.getAttribute("exception");

        if (exception instanceof JwtValidException) {
            resolver.resolveException(request, response, null, (JwtValidException) exception);
        } else if (exception instanceof WoologException){
            resolver.resolveException(request, response, null, (MemberNotExist) exception);
        } else {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
    }
}

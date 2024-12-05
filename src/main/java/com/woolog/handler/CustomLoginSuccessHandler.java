package com.woolog.handler;

import com.woolog.config.JwtTokenGenerator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.authenticator.SavedRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import java.io.IOException;

@Setter
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private JwtTokenGenerator jwtTokenGenerator;

    public CustomLoginSuccessHandler(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        setDefaultTargetUrl("/success");
        String email = (String) authentication.getPrincipal();
        String jwtToken = jwtTokenGenerator.generateJwtToken(email);
        response.setHeader("Authorization", jwtToken);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

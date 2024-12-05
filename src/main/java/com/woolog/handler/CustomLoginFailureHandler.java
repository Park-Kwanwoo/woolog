package com.woolog.handler;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final String FAILURE_URL = "/fail";

    public CustomLoginFailureHandler() {
        super(FAILURE_URL);
    }
}

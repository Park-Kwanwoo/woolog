package com.woolog.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Login {

    private final String email;
    private final String password;
}
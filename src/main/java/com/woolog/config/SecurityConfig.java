package com.woolog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.filter.CustomAuthenticationFilter;
import com.woolog.handler.CustomLoginFailureHandler;
import com.woolog.handler.CustomLoginSuccessHandler;
import com.woolog.repository.MemberRepository;
import com.woolog.security.CustomAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toStaticResources;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/posts").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/posts").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/posts").authenticated()
                        .anyRequest().permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationFilter jwtLoginFilter() {

        CustomAuthenticationFilter jwtAuthenticationFilter = new CustomAuthenticationFilter(objectMapper, authenticationManager());
        jwtAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        jwtAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler());

        return jwtAuthenticationFilter;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(memberRepository, passwordEncoder());
    }

    @Bean
    public JwtTokenGenerator jwtTokenGenerator() {
        return new JwtTokenGenerator();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomLoginSuccessHandler(jwtTokenGenerator());
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomLoginFailureHandler();
    }


    @Bean
    public AuthenticationManager authenticationManager()  {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
                .ignoring()
                .requestMatchers(toStaticResources().atCommonLocations())
                .requestMatchers(toH2Console());
    }
}

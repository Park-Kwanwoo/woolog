package com.woolog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woolog.security.filter.JsonLoginFilter;
import com.woolog.security.filter.JwtAuthenticationFilter;
import com.woolog.security.handler.CustomAccessDeniedHandler;
import com.woolog.security.handler.CustomEntryPoint;
import com.woolog.security.handler.CustomLoginFailureHandler;
import com.woolog.security.handler.CustomLoginSuccessHandler;
import com.woolog.repository.MemberRepository;
import com.woolog.security.handler.CustomAuthenticationProvider;
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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toStaticResources;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final HashEncrypt hashEncrypt;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin").hasAuthority("ADMIN")
                        .requestMatchers("/member").hasAuthority("MEMBER")
                        .requestMatchers("/posts/{postId}/comments", "POST").hasAnyAuthority("MEMBER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/posts").permitAll()
                        .requestMatchers(HttpMethod.POST, "/posts").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/posts").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/posts").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/posts/{postId}/comments").hasAnyAuthority("MEMBER", "ADMIN")
                        .anyRequest().permitAll())
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint()))
                .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandler()))
        ;

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JsonLoginFilter jwtLoginFilter() {

        JsonLoginFilter loginFilter = new JsonLoginFilter(objectMapper);
        loginFilter.setAuthenticationManager(authenticationManager());
        loginFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        loginFilter.setAuthenticationFailureHandler(authenticationFailureHandler());

        return loginFilter;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenGenerator);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(memberRepository, passwordEncoder());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomLoginSuccessHandler(jwtTokenGenerator, hashEncrypt, objectMapper);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomLoginFailureHandler(handlerExceptionResolver);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomEntryPoint(handlerExceptionResolver);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler(handlerExceptionResolver);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
                .ignoring()
                .requestMatchers(toStaticResources().atCommonLocations())
                .requestMatchers(toH2Console());
    }
}

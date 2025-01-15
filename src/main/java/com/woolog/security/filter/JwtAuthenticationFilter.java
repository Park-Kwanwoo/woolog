package com.woolog.security.filter;

import com.woolog.config.JwtTokenGenerator;
import com.woolog.domain.Member;
import com.woolog.exception.*;
import com.woolog.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenGenerator jwtTokenGenerator;
    private final SecurityContextHolderStrategy strategy = SecurityContextHolder.getContextHolderStrategy();
    private final MemberRepository memberRepository;
    private static final List<RequestMatcher> EXCLUDE_PATH_PATTERNS =
            List.of(new AntPathRequestMatcher("/posts/{postId}", "GET"),
                    new AntPathRequestMatcher("/posts/{postId}/comments", "GET"),
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/members/signup", "POST"),
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/auth/login", "POST"),
                    new AntPathRequestMatcher("/auth/logout", "GET"),
                    new RegexRequestMatcher("^/posts(\\?page=\\d+(&size=\\d+)?)?$", "GET")
            );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (isExclude(request)) {
            filterChain.doFilter(request, response);
        } else {
            String accessToken = request.getHeader("Authorization");
            String refreshToken = extractRefreshToken(request.getCookies());

            if (!StringUtils.hasText(accessToken) || !StringUtils.hasText(refreshToken)) {
                throw new InvalidTokenException();
            }

            try {

                Claims access = jwtTokenGenerator.getPayload(accessToken);
                Claims refresh = jwtTokenGenerator.getPayload(refreshToken);

                if (!jwtTokenGenerator.verifySubject(access, refresh) || !jwtTokenGenerator.verifyIssuer(access, refresh)) {
                    throw new JwtValidException();
                }

                String subject = access.getSubject();
                Member member = memberRepository.findByEmail(subject)
                        .orElseThrow(MemberNotExist::new);
                String email = member.getEmail();

                if (!email.equals(subject)) {
                    throw new MemberInfoNotValidException();
                }

                String role = member.getRole().name();
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
                UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken.authenticated(email, null, Collections.singletonList(authority));
                strategy.getContext().setAuthentication(authenticated);

            } catch (JwtException | MemberInfoNotValidException | MemberAuthenticationException e) {
                SecurityContextHolder.clearContext();
                request.setAttribute("exception", e);
            }

            filterChain.doFilter(request, response);
        }
    }

    private boolean isExclude(HttpServletRequest request) {
        for (RequestMatcher excludePathPattern : EXCLUDE_PATH_PATTERNS) {
            if (excludePathPattern.matches(request)) {
                return true;
            }
        }
        return false;
    }

    private String extractRefreshToken(Cookie[] cookies) {

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

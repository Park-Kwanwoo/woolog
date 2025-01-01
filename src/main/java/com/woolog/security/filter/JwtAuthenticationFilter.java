package com.woolog.security.filter;

import com.woolog.config.JwtTokenGenerator;
import com.woolog.domain.Member;
import com.woolog.exception.InvalidTokenException;
import com.woolog.exception.MemberInfoNotValidException;
import com.woolog.exception.MemberNotExist;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenGenerator jwtTokenGenerator;
    private Claims claims;
    private String refreshToken;

    private static final List<RequestMatcher> EXCLUDE_PATH_PATTERNS =
            List.of(new AntPathRequestMatcher("/posts/{postId}", "GET"),
                    new AntPathRequestMatcher("/h2-console/**"),
                    new AntPathRequestMatcher("/members/signup", "POST"),
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/auth/login", "POST"),
                    new RegexRequestMatcher("^/posts(\\?page=\\d+(&size=\\d+)?)?$", "GET")
            );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (isExclude(request)) {
            filterChain.doFilter(request, response);
        } else {
            String accessToken = request.getHeader("Authorization");
            Cookie[] cookies = request.getCookies();

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }

            if (accessToken.isEmpty() || refreshToken.isEmpty()) {
                throw new InvalidTokenException("token", "token 값이 존재하지 않습니다.");
            }

            try {
                if (jwtTokenGenerator.validateToken(accessToken)) {
                    claims = jwtTokenGenerator.parseToken(accessToken);
                } else if (jwtTokenGenerator.validateToken(refreshToken)) {
                    claims = jwtTokenGenerator.parseToken(refreshToken);
                }

                String subject = claims.getSubject();

                log.info("================== JwtAuthenticationFilter ==================");

                String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal.isEmpty() || !principal.equals(subject)) {
                    throw new MemberInfoNotValidException();
                }

            } catch (JwtException | MemberNotExist | MemberInfoNotValidException e) {
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
}

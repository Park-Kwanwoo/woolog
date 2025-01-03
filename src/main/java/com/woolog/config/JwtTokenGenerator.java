package com.woolog.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;


@Getter
@Component
public class JwtTokenGenerator {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-expiration-time}")
    private Long accessExpirationTime;

    @Value("${jwt.refresh-expiration-time}")
    private Long refreshExpirationTime;

    @Value("${jwt.issuer}")
    private String issuer;


    private SecretKey getSecretKey() {
        byte[] decodeKey = Base64.getDecoder().decode(this.secretKey);
        return Keys.hmacShaKeyFor(decodeKey);
    }

    public String generateAccessToken(String email) {
        return generateToken(email, this.getAccessExpirationTime());
    }

    public String generateRefreshToken(String email) {
        return generateToken(email, this.getRefreshExpirationTime());
    }

    public String generateToken(String email, Date expiration) {
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(email)
                .issuer(this.issuer)
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(this.getSecretKey())
                .compact();
    }

    public Date getAccessExpirationTime() {
        return Date.from(Instant.now().plus(this.accessExpirationTime, ChronoUnit.SECONDS));
    }

    public Date getRefreshExpirationTime() {
        return Date.from(Instant.now().plus(this.refreshExpirationTime, ChronoUnit.SECONDS));
    }

    public Claims getPayload(String accessToken) {
        try {
            return parseToken(accessToken).getPayload();
        } catch (JwtException e) {
            throw new JwtException(e.getMessage());
        }
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(this.getSecretKey())
                .build()
                .parseSignedClaims(token);
    }

    public boolean verifySubject(Claims access, Claims refresh) {
        return access.getSubject().equals(refresh.getSubject());
    }

    public boolean verifyIssuer(Claims access, Claims refresh) {
        return access.getIssuer().equals(this.issuer) && refresh.getIssuer().equals(this.issuer);
    }
}

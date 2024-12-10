package com.woolog.config;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
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
        byte[] decodeKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodeKey);
    }

    public String generateAccessToken(String email) {

        return Jwts.builder()
                .header()
                    .add("typ", "JWT")
                .and()
                .subject(email)
                .issuer(this.issuer)
                .issuedAt(new Date())
                .expiration(getAccessExpirationTime())
                .signWith(getSecretKey())
                .compact();
    }

    public String generateRefreshToken(String email) {

        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(email)
                .issuer(this.issuer)
                .issuedAt(new Date())
                .expiration(getRefreshExpirationTime())
                .signWith(getSecretKey())
                .compact();
    }

    protected Date getAccessExpirationTime() {
        return Date.from(Instant.now().plus(this.accessExpirationTime, ChronoUnit.SECONDS));
    }

    protected Date getRefreshExpirationTime() {
        return Date.from(Instant.now().plus(this.refreshExpirationTime, ChronoUnit.SECONDS));
    }

    public boolean verifyAccessToken(String accessToken) {
        SecretKey secretKey = getSecretKey();
        JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
        return parser.isSigned(accessToken);
    }

    public boolean verifyRefreshToken(String refreshToken) {
        SecretKey secretKey = getSecretKey();
        JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
        return parser.isSigned(refreshToken);
    }
}

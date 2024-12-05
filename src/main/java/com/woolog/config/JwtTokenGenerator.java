package com.woolog.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;


@Getter
public class JwtTokenGenerator {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private Long expirationTime;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt}")
    private String acc

    private SecretKey getSecretKey() {

        byte[] decodeKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodeKey);
    }

    public String generateJwtToken(String email) {

        return Jwts.builder()
                .header()
                    .add("typ", "JWT")
                .and()
                .subject(email)
                .issuer(this.issuer)
                .issuedAt(new Date())
                .expiration(getExpirationTime())
                .signWith(getSecretKey())
                .compact();
    }

    protected Date getExpirationTime() {
        Date now = new Date();
        return new Date(now.getTime() + this.expirationTime);
    }
}

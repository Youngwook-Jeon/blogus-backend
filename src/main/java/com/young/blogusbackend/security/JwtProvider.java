package com.young.blogusbackend.security;

import com.young.blogusbackend.model.Bloger;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final Environment env;
    private final String ACCESS_TOKEN_SECRET = "jwt.access_token_secret";
    private final String ACCESS_TOKEN_EXPIRATION_TIME = "jwt.access_token_expiration_time";
    private final String REFRESH_TOKEN_SECRET = "jwt.refresh_token_secret";
    private final String REFRESH_TOKEN_EXPIRATION_TIME = "jwt.refresh_token_expiration_time";

    public String generateAccessToken(Bloger bloger) {
        return generateToken(bloger, ACCESS_TOKEN_SECRET, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String generateRefreshToken(Bloger bloger) {
        return generateToken(bloger, REFRESH_TOKEN_SECRET, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private String generateToken(Bloger bloger, String secret, String expirationTime) {
        SecretKey secretKey =
                Keys.hmacShaKeyFor(env.getProperty(secret).getBytes(StandardCharsets.UTF_8));
        long jwtExpirationInMillis = Long.parseLong(env.getProperty(expirationTime));

        return Jwts.builder()
                .setId(String.valueOf(bloger.getId()))
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(secretKey)
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }
}

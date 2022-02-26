package com.young.blogusbackend.security;

import com.young.blogusbackend.model.Bloger;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final Environment env;
    public final static String ACCESS_TOKEN_SECRET = "jwt.access_token_secret";
    public final static String ACCESS_TOKEN_EXPIRATION_TIME = "jwt.access_token_expiration_time";
    public final static String REFRESH_TOKEN_SECRET = "jwt.refresh_token_secret";
    public final static String REFRESH_TOKEN_EXPIRATION_TIME = "jwt.refresh_token_expiration_time";

    public String generateAccessToken(Bloger bloger) {
        return generateToken(bloger, ACCESS_TOKEN_SECRET, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public String generateRefreshToken(Bloger bloger) {
        return generateToken(bloger, REFRESH_TOKEN_SECRET, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    private String generateToken(Bloger bloger, String secret, String expirationTime) {
        SecretKey secretKey = getSigningKey(secret);
        long jwtExpirationInMillis = Long.parseLong(env.getProperty(expirationTime));
        Claims claims = Jwts.claims();
        claims.put("id", bloger.getId());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(Instant.now()))
                .signWith(secretKey)
                .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
                .compact();
    }

    private SecretKey getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(env.getProperty(secret).getBytes(StandardCharsets.UTF_8));
    }

    public boolean validateTokenBySecret(String token, String secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(secret))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public Claims extractAllClaims(String token, String secret) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getDataFromJwt(String token, String secret) {
        return extractAllClaims(token, secret).get("id", Long.class);
    }
}

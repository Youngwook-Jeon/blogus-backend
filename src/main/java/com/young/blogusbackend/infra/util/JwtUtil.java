package com.young.blogusbackend.infra.util;

import com.young.blogusbackend.infra.config.AppProperties;
import com.young.blogusbackend.infra.constant.SecurityConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final long shortExpire = 5;
    private final AppProperties appProperties;

    public String generateActiveToken(String name, String account, String password) {
        return Jwts.builder()
                .claim("name", name)
                .claim("account", account)
                .claim("password", password)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(shortExpire).toInstant()))
                .signWith(
                        SignatureAlgorithm.HS512,
                        appProperties.getTokenSecret().getBytes(StandardCharsets.UTF_8)
                )
                .compact();
    }
}

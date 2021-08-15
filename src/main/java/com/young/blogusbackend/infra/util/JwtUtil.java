package com.young.blogusbackend.infra.util;

import com.young.blogusbackend.infra.constant.SecurityConstant;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Date;

public class JwtUtil {

    public final long shortExpire = 5;

    public String generateActiveToken(String name, String account, String password) {
        return Jwts.builder()
                .claim("name", name)
                .claim("account", account)
                .claim("password", password)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(shortExpire).toInstant()))
                .signWith(
                        SignatureAlgorithm.HS512,
//                        SecurityConstant.getTokenSecret().getBytes(StandardCharsets.UTF_8)
                        "abc".getBytes(StandardCharsets.UTF_8)
                )
                .compact();
    }
}

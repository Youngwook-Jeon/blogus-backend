package com.young.blogusbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final Environment env;

    public Cookie createCookie(String cookieName, String value) {
        long maxAge = Long.parseLong(env.getProperty("jwt.refresh_token_expiration_time"));
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/api/auth/refreshToken");
        cookie.setMaxAge((int) (maxAge / 1000));

        return cookie;
    }
}

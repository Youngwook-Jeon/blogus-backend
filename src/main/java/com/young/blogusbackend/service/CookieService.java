package com.young.blogusbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;

import static com.young.blogusbackend.security.JwtProvider.REFRESH_TOKEN_EXPIRATION_TIME;

@Service
@RequiredArgsConstructor
public class CookieService {

    private final Environment env;
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refreshtoken";

    public Cookie createRefreshTokenCookie(String value) {
        int maxAge = (int) (Long.parseLong(env.getProperty(REFRESH_TOKEN_EXPIRATION_TIME)) / 1000);
        return createCookie(REFRESH_TOKEN_COOKIE_NAME, value, "/", maxAge);
    }

    private Cookie createCookie(String cookieName, String value, String cookiePath, int maxAge) {
        Cookie cookie = new Cookie(cookieName, value);
        cookie.setHttpOnly(true);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(false);

        return cookie;
    }

    public Cookie deleteRefreshTokenCookie() {
        return deleteCookie(REFRESH_TOKEN_COOKIE_NAME);
    }

    private Cookie deleteCookie(String cookieName) {
        return createCookie(cookieName, null, "/", 0);
    }
}

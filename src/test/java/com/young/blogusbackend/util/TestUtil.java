package com.young.blogusbackend.util;

import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.Role;

import java.time.Instant;

public class TestUtil {

    public static final String VALID_USER_NAME = "Mayer";
    public static final String VALID_USER_EMAIL = "mayerjeon@gmail.com";
    public static final String VALID_USER_PASSWORD = "P4ssword!@#$";

    public static Bloger createValidUser() {
        return Bloger.builder()
                .name(VALID_USER_NAME)
                .email(VALID_USER_EMAIL)
                .password(VALID_USER_PASSWORD)
                .role(Role.ROLE_USER)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .enabled(true)
                .build();
    }

    public static Bloger createValidUserNotEnabled() {
        Bloger bloger = createValidUser();
        bloger.setEnabled(false);
        return bloger;
    }
}

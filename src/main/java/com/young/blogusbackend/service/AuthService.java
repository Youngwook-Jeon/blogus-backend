package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest registerRequest) {
        // TODO: register a user
    }
}

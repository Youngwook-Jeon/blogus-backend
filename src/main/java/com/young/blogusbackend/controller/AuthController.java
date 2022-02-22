package com.young.blogusbackend.controller;

import com.young.blogusbackend.dto.AuthenticationResponse;
import com.young.blogusbackend.dto.GenericResponse;
import com.young.blogusbackend.dto.LoginRequest;
import com.young.blogusbackend.dto.RegisterRequest;
import com.young.blogusbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse register(@Valid @RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        return new GenericResponse("등록에 성공했습니다. 이메일을 확인해주세요.");
    }

    @GetMapping("/accountVerification/{token}")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new GenericResponse("계정이 활성화되었습니다.");
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationResponse login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        authService.login(loginRequest);
        return null;
    }
}

package com.young.blogusbackend.controller;

import com.young.blogusbackend.dto.GenericResponse;
import com.young.blogusbackend.dto.RegisterRequest;
import com.young.blogusbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
}

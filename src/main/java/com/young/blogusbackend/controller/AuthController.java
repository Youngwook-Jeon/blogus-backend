package com.young.blogusbackend.controller;

import com.young.blogusbackend.dto.AuthenticationResponse;
import com.young.blogusbackend.dto.GenericResponse;
import com.young.blogusbackend.dto.LoginRequest;
import com.young.blogusbackend.dto.RegisterRequest;
import com.young.blogusbackend.service.AuthService;
import com.young.blogusbackend.service.CookieService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.young.blogusbackend.service.CookieService.REFRESH_TOKEN_COOKIE_NAME;

@RestController
@RequestMapping("/api/auth") @RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

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
        AuthenticationResponse authenticationResponse = authService.login(loginRequest);
        Cookie refreshToken = cookieService
                .createRefreshTokenCookie(authenticationResponse.getRefreshToken());
        response.addCookie(refreshToken);
        return authenticationResponse;
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<Object> refreshToken(
            @CookieValue(value = REFRESH_TOKEN_COOKIE_NAME, required = false) Cookie token,
            HttpServletResponse response
    ) {
        if (token != null) {
            AuthenticationResponse authenticationResponse =
                    authService.refreshToken(token.getValue());
            Cookie newToken = cookieService
                    .createRefreshTokenCookie(authenticationResponse.getRefreshToken());
            response.addCookie(newToken);
            return ResponseEntity.ok().body(authenticationResponse);
        }
        return ResponseEntity.badRequest().body(new GenericResponse("로그인이 필요합니다."));
    }

    @GetMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse logout(HttpServletResponse response) {
        authService.logout();
        Cookie deletedCookie = cookieService.deleteRefreshTokenCookie();
        response.addCookie(deletedCookie);
        return new GenericResponse("로그아웃되었습니다.");
    }
}

package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.AuthenticationResponse;
import com.young.blogusbackend.dto.LoginRequest;
import com.young.blogusbackend.dto.RegisterRequest;
import com.young.blogusbackend.exception.SpringBlogusException;
import com.young.blogusbackend.mapper.BlogerMapper;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.NotificationEmail;
import com.young.blogusbackend.model.Role;
import com.young.blogusbackend.model.VerificationToken;
import com.young.blogusbackend.repository.BlogerRepository;
import com.young.blogusbackend.repository.VerificationTokenRepository;
import com.young.blogusbackend.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final BlogerRepository blogerRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final Environment env;
    private final ITemplateEngine templateEngine;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final BlogerMapper blogerMapper;

    public void register(RegisterRequest registerRequest) {
        Bloger bloger = blogerMapper.registerRequestToBlog(registerRequest);
        Optional<Bloger> blogerOptional = blogerRepository.findByEmail(bloger.getEmail());
        if (blogerOptional.isPresent()) {
            throw new SpringBlogusException("이미 존재하는 유저입니다.");
        }
        bloger.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        blogerRepository.save(bloger);
        sendConfirmEmail(bloger);
    }

    private void sendConfirmEmail(Bloger bloger) {
        String client = env.getProperty("blogus.client");
        String token = generateVerificationToken(bloger);
        Context context = new Context();
        context.setVariable("name", bloger.getName());
        context.setVariable("link", client + "/activate/" + token);
        context.setVariable("linkName", "이메일 인증하기");
        context.setVariable("message", "블로거스에 가입하신 것을 환영합니다! 계정을 활성화하기 위해 아래 url을 클릭하세요: ");
        String message = templateEngine.process("mailTemplate", context);

        NotificationEmail email = NotificationEmail.builder()
                .recipient(bloger.getEmail())
                .subject("새 블로거스 계정을 활성화해주세요.")
                .body(message)
                .build();

        mailService.sendMail(email);
    }

    public String generateVerificationToken(Bloger bloger) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setBloger(bloger);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken =
                verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringBlogusException("유효한 토큰이 아닙니다."));
        fetchEnableUser(verificationToken.get());
    }

    private void fetchEnableUser(VerificationToken verificationToken) {
        Bloger bloger = verificationToken.getBloger();
        bloger.setEnabled(true);
        blogerRepository.save(bloger);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
             authentication = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    new User(
                                            loginRequest.getEmail(),
                                            loginRequest.getPassword(),
                                            Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.name()))
                                    ),
                                    loginRequest.getPassword()
                            )
                    );
        } catch (AuthenticationException e) {
            throw new SpringBlogusException("존재하지 않는 계정이거나 비밀번호가 일치하지 않습니다.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Bloger bloger = getCurrentUser();
        return getAuthenticationResponseWithMessage(bloger, "로그인이 성공했습니다!");
    }

    private AuthenticationResponse getAuthenticationResponseWithMessage(Bloger bloger, String message) {
        String refreshToken = jwtProvider.generateRefreshToken(bloger);
        bloger.setRefreshToken(refreshToken);
        blogerRepository.save(bloger);

        return AuthenticationResponse.builder()
                .msg(message)
                .accessToken(jwtProvider.generateAccessToken(bloger))
                .refreshToken(refreshToken)
                .user(blogerMapper.blogerToBlogerResponse(bloger))
                .build();
    }

    public Bloger getCurrentUser() {
        try {
            User principal = (User) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            return blogerRepository.findByEmail(principal.getUsername()).get();
        } catch (Exception e) {
            throw new UsernameNotFoundException("계정을 찾을 수 없습니다.");
        }
    }

    public AuthenticationResponse refreshToken(String token) {
        if (!(StringUtils.hasText(token) && jwtProvider.validateTokenBySecret(token, JwtProvider.REFRESH_TOKEN_SECRET))) {
            throw new SpringBlogusException("로그인이 필요합니다.");
        }
        Long blogerId = jwtProvider.getDataFromJwt(token, JwtProvider.REFRESH_TOKEN_SECRET);
        if (blogerId == null) {
            throw new SpringBlogusException("로그인이 필요합니다.");
        }

        Bloger bloger = blogerRepository.findById(blogerId)
                .orElseThrow(
                        () -> new SpringBlogusException("존재하지 않는 유저입니다.")
                );

        if (!token.equals(bloger.getRefreshToken())) {
            throw new SpringBlogusException("로그인이 필요합니다.");
        }
        return getAuthenticationResponseWithMessage(bloger, "ok");
    }

    public void logout() {
        Bloger currentUser = getCurrentUser();
        currentUser.setRefreshToken(null);
        blogerRepository.save(currentUser);
    }
}

package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.RegisterRequest;
import com.young.blogusbackend.model.NotificationEmail;
import com.young.blogusbackend.model.Role;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.VerificationToken;
import com.young.blogusbackend.repository.BlogerRepository;
import com.young.blogusbackend.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
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
    private final TemplateEngine templateEngine;

    public void register(RegisterRequest registerRequest) {
        Bloger bloger = new Bloger();
        bloger.setName(registerRequest.getName());
        bloger.setEmail(registerRequest.getEmail());
        bloger.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        bloger.setRole(Role.USER);
        bloger.setCreatedAt(Instant.now());
        bloger.setUpdatedAt(Instant.now());
        bloger.setEnabled(false);

        blogerRepository.save(bloger);
        sendConfirmEmail(bloger);
    }

    private void sendConfirmEmail(Bloger bloger) {
        String host = env.getProperty("blogus.host");
        String token = generateVerificationToken(bloger);
        Context context = new Context();
        context.setVariable("name", bloger.getName());
        context.setVariable("link", host + "/api/auth/accountVerification/" + token);
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

    private String generateVerificationToken(Bloger bloger) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setBloger(bloger);

        verificationTokenRepository.save(verificationToken);
        return token;
    }
}

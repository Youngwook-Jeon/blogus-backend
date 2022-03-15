package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.AuthenticationResponse;
import com.young.blogusbackend.dto.BlogerResponse;
import com.young.blogusbackend.dto.LoginRequest;
import com.young.blogusbackend.dto.RegisterRequest;
import com.young.blogusbackend.mapper.BlogerMapper;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.NotificationEmail;
import com.young.blogusbackend.model.Role;
import com.young.blogusbackend.model.VerificationToken;
import com.young.blogusbackend.repository.BlogerRepository;
import com.young.blogusbackend.repository.VerificationTokenRepository;
import com.young.blogusbackend.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class AuthServiceTests {

    @Mock
    private BlogerRepository mockBlogerRepository;
    @Mock
    private VerificationTokenRepository mockVerificationTokenRepository;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private MailService mockMailService;
    @Mock
    private Environment mockEnv;
    @Mock
    private ITemplateEngine mockTemplateEngine;
    @Mock
    private AuthenticationManager mockAuthenticationManager;
    @Mock
    private JwtProvider mockJwtProvider;
    @Mock
    private BlogerMapper mockBlogerMapper;

    @InjectMocks
    private AuthService authServiceUnderTest;

//    @BeforeEach
//    void setUp() {
//        authServiceUnderTest = new AuthService(mockBlogerRepository, mockVerificationTokenRepository,
//                mockPasswordEncoder, mockMailService, mockEnv, mockTemplateEngine, mockAuthenticationManager,
//                mockJwtProvider, mockBlogerMapper);
//    }

    @DisplayName("test for registration")
    @Test
    void testRegister() {
        // Setup
        final RegisterRequest registerRequest = new RegisterRequest("name", "email", "password", "cfPassword");

        // Configure BlogerMapper.registerRequestToBlog(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerMapper.registerRequestToBlog(registerRequest)).thenReturn(bloger);

        when(mockPasswordEncoder.encode("password")).thenReturn("password");

        // Configure BlogerRepository.save(...).
        when(mockBlogerRepository.save(bloger)).thenReturn(bloger);

        when(mockEnv.getProperty("blogus.client")).thenReturn("http://our-test-client.com");

        // Configure VerificationTokenRepository.save(...).
        final VerificationToken verificationToken = new VerificationToken(0L, "token",
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                ZoneOffset.UTC));
        when(mockVerificationTokenRepository.save(any(VerificationToken.class))).thenReturn(verificationToken);

        when(mockTemplateEngine.process(eq("mailTemplate"), any(IContext.class))).thenReturn("body");

        // Run the test
        authServiceUnderTest.register(registerRequest);

        // Verify the results
        verify(mockBlogerRepository).save(bloger);
        verify(mockVerificationTokenRepository).save(any(VerificationToken.class));
        verify(mockMailService).sendMail(any(NotificationEmail.class));
    }

    @Test
    void testRegister_EnvironmentReturnsNull() {
        // Setup
        final RegisterRequest registerRequest = new RegisterRequest("name", "email", "password", "cfPassword");

        // Configure BlogerMapper.registerRequestToBlog(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerMapper.registerRequestToBlog(
                new RegisterRequest("name", "email", "password", "cfPassword"))).thenReturn(bloger);

        when(mockPasswordEncoder.encode("password")).thenReturn("password");

        // Configure BlogerRepository.save(...).
        final Bloger bloger1 = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(bloger1);

        when(mockEnv.getProperty("blogus.client")).thenReturn(null);

        // Configure VerificationTokenRepository.save(...).
        final VerificationToken verificationToken = new VerificationToken(0L, "token",
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                ZoneOffset.UTC));
        when(mockVerificationTokenRepository.save(new VerificationToken(0L, "token",
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                ZoneOffset.UTC)))).thenReturn(verificationToken);

        when(mockTemplateEngine.process(eq("mailTemplate"), any(IContext.class))).thenReturn("body");

        // Run the test
        authServiceUnderTest.register(registerRequest);

        // Verify the results
        verify(mockBlogerRepository).save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
        verify(mockVerificationTokenRepository).save(new VerificationToken(0L, "token",
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                ZoneOffset.UTC)));
        verify(mockMailService).sendMail(new NotificationEmail("subject", "recipient", "body"));
    }

    @Test
    void testVerifyAccount() {
        // Setup
        // Configure VerificationTokenRepository.findByToken(...).
        final Optional<VerificationToken> verificationToken = Optional.of(new VerificationToken(0L, "token",
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                ZoneOffset.UTC)));
        when(mockVerificationTokenRepository.findByToken("token")).thenReturn(verificationToken);

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(bloger);

        // Run the test
        authServiceUnderTest.verifyAccount("token");

        // Verify the results
        verify(mockBlogerRepository).save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
    }

    @Test
    void testVerifyAccount_VerificationTokenRepositoryReturnsAbsent() {
        // Setup
        when(mockVerificationTokenRepository.findByToken("token")).thenReturn(Optional.empty());

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(bloger);

        // Run the test
        authServiceUnderTest.verifyAccount("token");

        // Verify the results
        verify(mockBlogerRepository).save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
    }

    @Test
    void testLogin() {
        // Setup
        final LoginRequest loginRequest = new LoginRequest("email", "password");
        final AuthenticationResponse expectedResult = new AuthenticationResponse("msg", "accessToken", "refreshToken",
                new BlogerResponse(0L, "name", "email", "avatar", "role", false, "createdAt"));
        when(mockAuthenticationManager.authenticate(null)).thenReturn(null);

        // Configure BlogerRepository.findByEmail(...).
        final Optional<Bloger> blogerOptional = Optional.of(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
        when(mockBlogerRepository.findByEmail("username")).thenReturn(blogerOptional);

        when(mockJwtProvider.generateRefreshToken(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn("refreshToken");

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(bloger);

        when(mockJwtProvider.generateAccessToken(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn("accessToken");

        // Configure BlogerMapper.blogerToBlogerResponse(...).
        final BlogerResponse blogerResponse = new BlogerResponse(0L, "name", "email", "avatar", "role", false,
                "createdAt");
        when(mockBlogerMapper.blogerToBlogerResponse(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(blogerResponse);

        // Run the test
        final AuthenticationResponse result = authServiceUnderTest.login(loginRequest);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockBlogerRepository).save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
    }

    @Test
    void testLogin_AuthenticationManagerThrowsAuthenticationException() {
        // Setup
        final LoginRequest loginRequest = new LoginRequest("email", "password");
        final AuthenticationResponse expectedResult = new AuthenticationResponse("msg", "accessToken", "refreshToken",
                new BlogerResponse(0L, "name", "email", "avatar", "role", false, "createdAt"));
        when(mockAuthenticationManager.authenticate(null)).thenThrow(AuthenticationException.class);

        // Configure BlogerRepository.findByEmail(...).
        final Optional<Bloger> blogerOptional = Optional.of(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
        when(mockBlogerRepository.findByEmail("username")).thenReturn(blogerOptional);

        when(mockJwtProvider.generateRefreshToken(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn("refreshToken");

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(bloger);

        when(mockJwtProvider.generateAccessToken(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn("accessToken");

        // Configure BlogerMapper.blogerToBlogerResponse(...).
        final BlogerResponse blogerResponse = new BlogerResponse(0L, "name", "email", "avatar", "role", false,
                "createdAt");
        when(mockBlogerMapper.blogerToBlogerResponse(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(blogerResponse);

        // Run the test
        final AuthenticationResponse result = authServiceUnderTest.login(loginRequest);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockBlogerRepository).save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
    }

    @Test
    void testLogin_BlogerRepositoryFindByEmailReturnsAbsent() {
        // Setup
        final LoginRequest loginRequest = new LoginRequest("email", "password");
        final AuthenticationResponse expectedResult = new AuthenticationResponse("msg", "accessToken", "refreshToken",
                new BlogerResponse(0L, "name", "email", "avatar", "role", false, "createdAt"));
        when(mockAuthenticationManager.authenticate(null)).thenReturn(null);
        when(mockBlogerRepository.findByEmail("username")).thenReturn(Optional.empty());
        when(mockJwtProvider.generateRefreshToken(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn("refreshToken");

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(bloger);

        when(mockJwtProvider.generateAccessToken(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn("accessToken");

        // Configure BlogerMapper.blogerToBlogerResponse(...).
        final BlogerResponse blogerResponse = new BlogerResponse(0L, "name", "email", "avatar", "role", false,
                "createdAt");
        when(mockBlogerMapper.blogerToBlogerResponse(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(blogerResponse);

        // Run the test
        final AuthenticationResponse result = authServiceUnderTest.login(loginRequest);

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockBlogerRepository).save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
    }

    @Test
    void testGetCurrentUser() {
        // Setup
        final Bloger expectedResult = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER,
                "refreshToken", LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);

        // Configure BlogerRepository.findByEmail(...).
        final Optional<Bloger> blogerOptional = Optional.of(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
        when(mockBlogerRepository.findByEmail("username")).thenReturn(blogerOptional);

        // Run the test
        final Bloger result = authServiceUnderTest.getCurrentUser();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testGetCurrentUser_BlogerRepositoryReturnsAbsent() {
        // Setup
        final Bloger expectedResult = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER,
                "refreshToken", LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.findByEmail("username")).thenReturn(Optional.empty());

        // Run the test
        final Bloger result = authServiceUnderTest.getCurrentUser();

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testRefreshToken() {
        // Setup
        final AuthenticationResponse expectedResult = new AuthenticationResponse("msg", "accessToken", "refreshToken",
                new BlogerResponse(0L, "name", "email", "avatar", "role", false, "createdAt"));
        when(mockJwtProvider.validateTokenBySecret("token", "jwt.refresh_token_secret")).thenReturn(false);
        when(mockJwtProvider.getDataFromJwt("token", "jwt.refresh_token_secret")).thenReturn(0L);

        // Configure BlogerRepository.findById(...).
        final Optional<Bloger> blogerOptional = Optional.of(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
        when(mockBlogerRepository.findById(0L)).thenReturn(blogerOptional);

        when(mockJwtProvider.generateRefreshToken(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn("refreshToken");

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(bloger);

        when(mockJwtProvider.generateAccessToken(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn("accessToken");

        // Configure BlogerMapper.blogerToBlogerResponse(...).
        final BlogerResponse blogerResponse = new BlogerResponse(0L, "name", "email", "avatar", "role", false,
                "createdAt");
        when(mockBlogerMapper.blogerToBlogerResponse(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(blogerResponse);

        // Run the test
        final AuthenticationResponse result = authServiceUnderTest.refreshToken("token");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockBlogerRepository).save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
    }

    @Test
    void testRefreshToken_BlogerRepositoryFindByIdReturnsAbsent() {
        // Setup
        final AuthenticationResponse expectedResult = new AuthenticationResponse("msg", "accessToken", "refreshToken",
                new BlogerResponse(0L, "name", "email", "avatar", "role", false, "createdAt"));
        when(mockJwtProvider.validateTokenBySecret("token", "jwt.refresh_token_secret")).thenReturn(false);
        when(mockJwtProvider.getDataFromJwt("token", "jwt.refresh_token_secret")).thenReturn(0L);
        when(mockBlogerRepository.findById(0L)).thenReturn(Optional.empty());
        when(mockJwtProvider.generateRefreshToken(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn("refreshToken");

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(bloger);

        when(mockJwtProvider.generateAccessToken(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn("accessToken");

        // Configure BlogerMapper.blogerToBlogerResponse(...).
        final BlogerResponse blogerResponse = new BlogerResponse(0L, "name", "email", "avatar", "role", false,
                "createdAt");
        when(mockBlogerMapper.blogerToBlogerResponse(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(blogerResponse);

        // Run the test
        final AuthenticationResponse result = authServiceUnderTest.refreshToken("token");

        // Verify the results
        assertThat(result).isEqualTo(expectedResult);
        verify(mockBlogerRepository).save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
    }

    @Test
    void testLogout() {
        // Setup
        // Configure BlogerRepository.findByEmail(...).
        final Optional<Bloger> blogerOptional = Optional.of(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
        when(mockBlogerRepository.findByEmail("username")).thenReturn(blogerOptional);

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(bloger);

        // Run the test
        authServiceUnderTest.logout();

        // Verify the results
        verify(mockBlogerRepository).save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
    }

    @Test
    void testLogout_BlogerRepositoryFindByEmailReturnsAbsent() {
        // Setup
        when(mockBlogerRepository.findByEmail("username")).thenReturn(Optional.empty());

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false))).thenReturn(bloger);

        // Run the test
        authServiceUnderTest.logout();

        // Verify the results
        verify(mockBlogerRepository).save(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
    }
}

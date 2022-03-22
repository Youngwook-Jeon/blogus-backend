package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.AuthenticationResponse;
import com.young.blogusbackend.dto.BlogerResponse;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.IContext;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

        when(mockBlogerRepository.findByEmail(bloger.getEmail())).thenReturn(Optional.empty());

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

    @DisplayName("test for registration when a user is already in DB")
    @Test
    void testRegister_whenRequestedUserIsAlreadyInDB() {
        // Setup
        final RegisterRequest registerRequest = new RegisterRequest("name", "email", "password", "cfPassword");

        // Configure BlogerMapper.registerRequestToBlog(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerMapper.registerRequestToBlog(registerRequest)).thenReturn(bloger);

        when(mockBlogerRepository.findByEmail(bloger.getEmail())).thenReturn(Optional.of(bloger));

        // Run the test
        assertThrows(SpringBlogusException.class, () -> authServiceUnderTest.register(registerRequest));

        // Verify the results
        verify(mockBlogerRepository, never()).save(any(Bloger.class));
        verify(mockMailService, never()).sendMail(any(NotificationEmail.class));
    }

    @DisplayName("test for verifying an account if token exists")
    @Test
    void testVerifyAccount() {
        // Setup
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);

        // Configure VerificationTokenRepository.findByToken(...).
        final Optional<VerificationToken> verificationToken = Optional.of(new VerificationToken(0L, "token", bloger, LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC)));
        when(mockVerificationTokenRepository.findByToken("token")).thenReturn(verificationToken);

        // Configure BlogerRepository.save(...).
        when(mockBlogerRepository.save(bloger)).thenReturn(bloger);

        // Run the test
        authServiceUnderTest.verifyAccount("token");

        // Verify the results
        verify(mockBlogerRepository).save(bloger);
        assertThat(bloger.isEnabled()).isTrue();
    }

    @DisplayName("test for verifying an account if token does not exist")
    @Test
    void testVerifyAccount_VerificationTokenRepositoryReturnsAbsent() {
        // Setup
        when(mockVerificationTokenRepository.findByToken("token")).thenReturn(Optional.empty());

        // Run the test
        assertThrows(SpringBlogusException.class, () -> authServiceUnderTest.verifyAccount("token"));

        // Verify the results
        verify(mockBlogerRepository, never()).save(any(Bloger.class));
    }

    @DisplayName("test for login with successful auth response")
    @Test
    void testLogin_whenAuthenticateByCorrectLoginRequest_returnsAuthResponse() {
        // Setup
        final LoginRequest loginRequest = new LoginRequest("mayerjeon@gmail.com", "password");
        final Bloger bloger = new Bloger(0L, "name", "mayerjeon@gmail.com", "password", "avatar", Role.ROLE_USER, null,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), true);
        Authentication authentication = new TestingAuthenticationToken(new User("mayerjeon@gmail.com", "password", Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.name()))), "password");
        when(mockAuthenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(mockBlogerRepository.findByEmail("mayerjeon@gmail.com")).thenReturn(Optional.of(bloger));
        when(mockJwtProvider.generateRefreshToken(any(Bloger.class))).thenReturn("refreshToken");

        // Configure BlogerRepository.save(...).
        when(mockBlogerRepository.save(any(Bloger.class))).thenReturn(bloger);

        when(mockJwtProvider.generateAccessToken(any(Bloger.class))).thenReturn("accessToken");

        // Configure BlogerMapper.blogerToBlogerResponse(...).
        final BlogerResponse blogerResponse = new BlogerResponse(0L, "name", "mayerjeon@gmail.com", "avatar", Role.ROLE_USER.name(), true, "createdAt");
        when(mockBlogerMapper.blogerToBlogerResponse(any(Bloger.class))).thenReturn(blogerResponse);

        // Run the test
        final AuthenticationResponse result = authServiceUnderTest.login(loginRequest);

        // Verify the results
        assertThat(result.getUser().getEmail()).isEqualTo("mayerjeon@gmail.com");
        assertThat(result.getUser().isEnabled()).isTrue();
        assertThat(result.getUser().getRole()).isEqualTo(Role.ROLE_USER.name());
        assertThat(result.getUser().getName()).isEqualTo("name");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        verify(mockBlogerRepository).save(any(Bloger.class));
    }

    @DisplayName("test for login when the given request is wrong")
    @Test
    void testLogin_whenGivenRequestIsWrong() {
        // Setup
        final LoginRequest loginRequest = new LoginRequest("email", "password");

        when(mockAuthenticationManager.authenticate(any(Authentication.class))).thenThrow(SpringBlogusException.class);

        // Run the test
        assertThrows(SpringBlogusException.class, () -> authServiceUnderTest.login(loginRequest));

        // Verify the results
        verify(mockBlogerRepository, never()).save(any(Bloger.class));
    }

    @DisplayName("test for getting current user")
    @Test
    void testGetCurrentUser() {
        // Setup
        final Authentication authentication = new UsernamePasswordAuthenticationToken(new User("mayerjeon@gmail.com", "password", Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.name()))), "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final Bloger bloger = new Bloger(0L, "name", "mayerjeon@gmail.com", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        // Configure BlogerRepository.findByEmail(...)
        when(mockBlogerRepository.findByEmail("mayerjeon@gmail.com")).thenReturn(Optional.of(bloger));

        // Run the test
        Bloger currentUser = authServiceUnderTest.getCurrentUser();

        // Verify the results
        assertThat(currentUser).isEqualTo(bloger);
    }

    @DisplayName("test for getting current user when the authentication is null")
    @Test
    void testGetCurrentUser_whenAuthenticationIsNull() {
        // Setup
        SecurityContextHolder.getContext().setAuthentication(null);

        // Run the test
        assertThrows(UsernameNotFoundException.class, () -> authServiceUnderTest.getCurrentUser());

        // Verify the results
        verify(mockBlogerRepository, never()).findByEmail(any(String.class));
    }

    @DisplayName("test for valid refresh token")
    @Test
    void testRefreshToken() {
        // Setup
        final AuthenticationResponse expectedResult = new AuthenticationResponse("msg", "accessToken", "refreshToken",
                new BlogerResponse(0L, "name", "email", "avatar", "role", false, "createdAt"));
        when(mockJwtProvider.validateTokenBySecret("refreshToken", JwtProvider.REFRESH_TOKEN_SECRET)).thenReturn(true);
        when(mockJwtProvider.getDataFromJwt("refreshToken", JwtProvider.REFRESH_TOKEN_SECRET)).thenReturn(0L);

        // Configure BlogerRepository.findById(...).
        final Optional<Bloger> blogerOptional = Optional.of(
                new Bloger(0L, "name", "mayerjeon@gmail.com", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        true));
        when(mockBlogerRepository.findById(0L)).thenReturn(blogerOptional);

        when(mockJwtProvider.generateRefreshToken(any(Bloger.class))).thenReturn("newRefreshToken");

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "mayerjeon@gmail.com", "password", "avatar", Role.ROLE_USER, "newRefreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), true);
        when(mockBlogerRepository.save(any(Bloger.class))).thenReturn(bloger);

        when(mockJwtProvider.generateAccessToken(any(Bloger.class))).thenReturn("newAccessToken");

        // Configure BlogerMapper.blogerToBlogerResponse(...).
        final BlogerResponse blogerResponse = new BlogerResponse(0L, "name", "mayerjeon@gmail.com", "avatar", Role.ROLE_USER.name(), true,
                "createdAt");
        when(mockBlogerMapper.blogerToBlogerResponse(any(Bloger.class))).thenReturn(blogerResponse);

        // Run the test
        final AuthenticationResponse result = authServiceUnderTest.refreshToken("refreshToken");

        // Verify the results
        assertThat(result.getUser().getEmail()).isEqualTo("mayerjeon@gmail.com");
        assertThat(result.getUser().isEnabled()).isTrue();
        assertThat(result.getUser().getRole()).isEqualTo(Role.ROLE_USER.name());
        assertThat(result.getUser().getName()).isEqualTo("name");
        assertThat(result.getRefreshToken()).isEqualTo("newRefreshToken");
        assertThat(result.getAccessToken()).isEqualTo("newAccessToken");
        verify(mockBlogerRepository).save(any(Bloger.class));
    }

    @DisplayName("test for invalid refresh token")
    @Test
    void testRefreshToken_givenRefreshTokenIsInvalid_thenThrowsException() {
        // Setup
        when(mockJwtProvider.validateTokenBySecret("INVALID_TOKEN", JwtProvider.REFRESH_TOKEN_SECRET)).thenThrow(SpringBlogusException.class);

        // Run the test
        assertThrows(SpringBlogusException.class, () -> authServiceUnderTest.refreshToken("INVALID_TOKEN"));

        // Verify
        verify(mockBlogerRepository, never()).findById(any(Long.class));
    }

    @DisplayName("test for logout")
    @Test
    void testLogout() {
        // Setup
        final Authentication authentication = new UsernamePasswordAuthenticationToken(new User("mayerjeon@gmail.com", "password", Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.name()))), "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Configure BlogerRepository.findByEmail(...).
        final Optional<Bloger> blogerOptional = Optional.of(
                new Bloger(0L, "name", "mayerjeon@gmail.com", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        true));
        when(mockBlogerRepository.findByEmail("mayerjeon@gmail.com")).thenReturn(blogerOptional);

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "mayerjeon@gmail.com", "password", "avatar", Role.ROLE_USER, null,
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), true);
        when(mockBlogerRepository.save(any(Bloger.class))).thenReturn(bloger);

        // Run the test
        authServiceUnderTest.logout();

        // Verify the results
        verify(mockBlogerRepository, times(1)).save(any(Bloger.class));
    }

}

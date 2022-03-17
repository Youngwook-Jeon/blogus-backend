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

    @DisplayName("test for login")
    @Test
    void testLogin() {
        // Setup
        final LoginRequest loginRequest = new LoginRequest("email", "password");

        final Authentication authentication = new UsernamePasswordAuthenticationToken(new User(loginRequest.getEmail(), loginRequest.getPassword(), Collections.singleton(new SimpleGrantedAuthority(Role.ROLE_USER.name()))), loginRequest.getPassword());

        when(mockAuthenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);

        // Configure BlogerRepository.findByEmail(...).
        final Optional<Bloger> blogerOptional = Optional.of(
                new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                        LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                                ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC),
                        false));
        when(mockBlogerRepository.findByEmail("email")).thenReturn(blogerOptional);

        when(mockJwtProvider.generateRefreshToken(any(Bloger.class))).thenReturn("refreshToken");

        // Configure BlogerRepository.save(...).
        final Bloger bloger = new Bloger(0L, "name", "email", "password", "avatar", Role.ROLE_USER, "refreshToken",
                LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(
                        ZoneOffset.UTC), LocalDateTime.of(2020, 1, 1, 0, 0, 0, 0).toInstant(ZoneOffset.UTC), false);
        when(mockBlogerRepository.save(any(Bloger.class))).thenReturn(bloger);

        when(mockJwtProvider.generateAccessToken(any(Bloger.class))).thenReturn("accessToken");

        // Configure BlogerMapper.blogerToBlogerResponse(...).
        final BlogerResponse blogerResponse = new BlogerResponse(0L, "name", "email", "avatar", "role", false,
                "createdAt");
        when(mockBlogerMapper.blogerToBlogerResponse(any(Bloger.class))).thenReturn(blogerResponse);

        // Run the test
        final AuthenticationResponse result = authServiceUnderTest.login(loginRequest);

        // Verify the results
        verify(mockBlogerRepository).save(any(Bloger.class));
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(result.getUser().getEmail()).isEqualTo("email");
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

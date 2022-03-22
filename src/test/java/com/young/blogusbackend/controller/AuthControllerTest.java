package com.young.blogusbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.young.blogusbackend.dto.LoginRequest;
import com.young.blogusbackend.dto.RegisterRequest;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.NotificationEmail;
import com.young.blogusbackend.repository.BlogerRepository;
import com.young.blogusbackend.security.JwtProvider;
import com.young.blogusbackend.service.AuthService;
import com.young.blogusbackend.service.CookieService;
import com.young.blogusbackend.service.MailService;
import com.young.blogusbackend.util.MockMvcTest;
import com.young.blogusbackend.util.TestUtil;
import com.young.blogusbackend.util.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BlogerRepository blogerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CookieService cookieService;

    @MockBean
    MailService mailService;

    @DisplayName("test for register with correct request")
    @Test
    void testRegister() throws Exception {
        // Setup
        RegisterRequest registerRequest =
                new RegisterRequest("mayer", "mayerjeon@gmail.com", "P4ssword!@#$", "P4ssword!@#$");

        // Run the test
        ResultActions resultActions = mockMvc.perform(post("/api/auth/register")
                .content(objectMapper.writeValueAsString(registerRequest)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Verify the results
        Optional<Bloger> blogerOptional = blogerRepository.findByEmail("mayerjeon@gmail.com");
        assertThat(blogerOptional.isPresent()).isTrue();
        assertThat(blogerOptional.get().getPassword()).isNotEqualTo("P4ssword!@#$");
        then(mailService).should().sendMail(any(NotificationEmail.class));

        resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg", is("등록에 성공했습니다. 이메일을 확인해주세요.")));
    }

    @DisplayName("test for register with wrong request")
    @Test
    void testRegister_givenRequestIsWrong_returnsWithBadRequestStatus() throws Exception {
        // Setup
        RegisterRequest registerRequest =
                new RegisterRequest("m", "wrongemail@", "P4ssword!@#$", "P4ssword");

        // Run the test
        ResultActions resultActions = mockMvc.perform(post("/api/auth/register")
                .content(objectMapper.writeValueAsString(registerRequest)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Verify the results
        List<Bloger> blogers = blogerRepository.findAll();
        assertThat(blogers.size()).isEqualTo(0);

        resultActions.andDo(print())
                .andExpect(status().isBadRequest());
    }

    @DisplayName("test for verify account")
    @Test
    void testVerifyAccount() throws Exception {
        // Setup
        Bloger bloger = TestUtil.createValidUserNotEnabled();
        blogerRepository.save(bloger);
        String token = authService.generateVerificationToken(bloger);

        // Run the test
        ResultActions resultActions =
                mockMvc.perform(get("/api/auth/accountVerification/{token}", token)
                        .accept(MediaType.APPLICATION_JSON));

        // Verify the results
        Optional<Bloger> blogerOptional = blogerRepository.findByEmail(TestUtil.VALID_USER_EMAIL);
        assertThat(blogerOptional.isPresent()).isTrue();
        assertThat(blogerOptional.get().isEnabled()).isTrue();

        resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg", is("계정이 활성화되었습니다.")));
    }

    @DisplayName("test for login with correct login request")
    @Test
    void testLogin() throws Exception {
        // Setup
        Bloger bloger = TestUtil.createValidUser();
        bloger.setPassword(passwordEncoder.encode(bloger.getPassword()));
        blogerRepository.save(bloger);
        LoginRequest loginRequest = new LoginRequest("mayerjeon@gmail.com", "P4ssword!@#$");

        // Run the test
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Verify the results
        resultActions.andDo(print())
                .andExpect(cookie().exists(CookieService.REFRESH_TOKEN_COOKIE_NAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg", is("로그인이 성공했습니다!")))
                .andExpect(jsonPath("$.user.email", is("mayerjeon@gmail.com")))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
    }

    @DisplayName("test for login when credential does not match with our DB data")
    @Test
    void testLogin_givenLoginRequestIsIncorrect_returnsWithBadRequestStatus() throws Exception {
        // Setup
        Bloger bloger = TestUtil.createValidUser();
        bloger.setPassword(passwordEncoder.encode(bloger.getPassword()));
        blogerRepository.save(bloger);
        LoginRequest loginRequest = new LoginRequest("mayerjeon@gmail.com", "WRONG_PASSWORD123!!");

        // Run the test
        ResultActions resultActions = mockMvc.perform(post("/api/auth/login")
                .content(objectMapper.writeValueAsString(loginRequest)).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        // Verify the results
        resultActions.andDo(print())
                .andExpect(cookie().doesNotExist(CookieService.REFRESH_TOKEN_COOKIE_NAME))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg", is("존재하지 않는 계정이거나 비밀번호가 일치하지 않습니다.")));
    }

    @DisplayName("test for refresh token with no cookie")
    @Test
    void testRefreshToken_whenTheRequestHasNoCookie_returnsWithBadRequestStatus() throws Exception {
        // Setup
        // Run the test
        ResultActions resultActions = mockMvc.perform(get("/api/auth/refreshToken")
                .accept(MediaType.APPLICATION_JSON));

        // Verify the results
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.msg", is("로그인이 필요합니다.")));
    }

    @DisplayName("test for refresh token with a cookie")
    @Test
    void testRefreshToken() throws Exception {
        // Setup
        Bloger bloger = TestUtil.createValidUser();
        blogerRepository.save(bloger); // id is needed
        String refreshToken = jwtProvider.generateRefreshToken(bloger);
        bloger.setRefreshToken(refreshToken);
        blogerRepository.save(bloger);

        // Run the test
        ResultActions resultActions = mockMvc.perform(get("/api/auth/refreshToken")
                .cookie(cookieService.createRefreshTokenCookie(refreshToken))
                .accept(MediaType.APPLICATION_JSON));

        // Verify the results
        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().exists(CookieService.REFRESH_TOKEN_COOKIE_NAME))
                .andExpect(jsonPath("$.msg", is("ok")))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists());
    }

    @DisplayName("test for logout")
    @Test
    @WithMockCustomUser
    void testLogout() throws Exception {
        // Setup
        Bloger bloger = TestUtil.createValidUser();
        bloger.setPassword(passwordEncoder.encode(bloger.getPassword()));
        blogerRepository.save(bloger);

        // Run the test
        ResultActions resultActions = mockMvc.perform(get("/api/auth/logout")
                .accept(MediaType.APPLICATION_JSON));

        // Verify the results
        Optional<Bloger> blogerOptional = blogerRepository.findByEmail("mayerjeon@gmail.com");
        assertThat(blogerOptional.isPresent()).isTrue();
        assertThat(blogerOptional.get().getRefreshToken()).isNull();

        resultActions.andDo(print())
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge(CookieService.REFRESH_TOKEN_COOKIE_NAME, 0))
                .andExpect(jsonPath("$.msg", is("로그아웃되었습니다.")));
    }
}

package com.young.blogusbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.young.blogusbackend.dto.RegisterRequest;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.NotificationEmail;
import com.young.blogusbackend.repository.BlogerRepository;
import com.young.blogusbackend.service.AuthService;
import com.young.blogusbackend.service.MailService;
import com.young.blogusbackend.util.MockMvcTest;
import com.young.blogusbackend.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

//    @Test
//    void testLogin() throws Exception {
//        // Setup
//        // Configure AuthService.login(...).
//        final AuthenticationResponse authenticationResponse = new AuthenticationResponse("msg", "accessToken",
//                "refreshToken", new BlogerResponse(0L, "name", "email", "avatar", "role", false, "createdAt"));
//        when(mockAuthService.login(new LoginRequest("email", "password"))).thenReturn(authenticationResponse);
//
//        when(mockCookieService.createRefreshTokenCookie("refreshToken")).thenReturn(new Cookie("name", "value"));
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(post("/api/auth/login")
//                        .content("content").contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testRefreshToken() throws Exception {
//        // Setup
//        // Configure AuthService.refreshToken(...).
//        final AuthenticationResponse authenticationResponse = new AuthenticationResponse("msg", "accessToken",
//                "refreshToken", new BlogerResponse(0L, "name", "email", "avatar", "role", false, "createdAt"));
//        when(mockAuthService.refreshToken("value")).thenReturn(authenticationResponse);
//
//        when(mockCookieService.createRefreshTokenCookie("refreshToken")).thenReturn(new Cookie("name", "value"));
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/api/auth/refreshToken")
//                        .cookie(new Cookie("REFRESH_TOKEN_COOKIE_NAME", "REFRESH_TOKEN_COOKIE_NAME"))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//    }
//
//    @Test
//    void testLogout() throws Exception {
//        // Setup
//        when(mockCookieService.deleteRefreshTokenCookie()).thenReturn(new Cookie("name", "value"));
//
//        // Run the test
//        final MockHttpServletResponse response = mockMvc.perform(get("/api/auth/logout")
//                        .accept(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse();
//
//        // Verify the results
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
//        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
//        verify(mockAuthService).logout();
//    }
}

package com.digitalpayment.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.digitalpayment.auth.dto.*;
import com.digitalpayment.auth.entity.Role;
import com.digitalpayment.auth.service.IAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock private IAuthService authService;

  @InjectMocks private com.digitalpayment.auth.controller.AuthController authController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;
  private AuthResponse authResponse;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    objectMapper = new ObjectMapper();

    authResponse =
        new AuthResponse(
            "jwt-token",
            "refresh-token",
            "Bearer",
            86400L,
            UUID.randomUUID(),
            "john@example.com",
            Role.USER);
  }

  @Test
  void testRegisterSuccess() throws Exception {
    RegisterRequest request = new RegisterRequest("John", "Doe", "john@example.com", "password123");

    when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("jwt-token"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").value(86400L))
        .andExpect(jsonPath("$.email").value("john@example.com"));

    verify(authService).register(any(RegisterRequest.class));
  }

  @Test
  void testRegisterInvalidEmail() throws Exception {
    RegisterRequest request = new RegisterRequest("John", "Doe", "invalid-email", "password123");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testRegisterShortPassword() throws Exception {
    RegisterRequest request = new RegisterRequest("John", "Doe", "john@example.com", "short");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testRegisterMissingFirstName() throws Exception {
    RegisterRequest request = new RegisterRequest("", "Doe", "john@example.com", "password123");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testRegisterMissingLastName() throws Exception {
    RegisterRequest request = new RegisterRequest("John", "", "john@example.com", "password123");

    mockMvc
        .perform(
            post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testLoginSuccess() throws Exception {
    LoginRequest request = new LoginRequest("john@example.com", "password123");

    when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("jwt-token"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").value(86400L))
        .andExpect(jsonPath("$.email").value("john@example.com"));

    verify(authService).login(any(LoginRequest.class));
  }

  @Test
  void testLoginInvalidEmail() throws Exception {
    LoginRequest request = new LoginRequest("invalid-email", "password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testLoginMissingPassword() throws Exception {
    LoginRequest request = new LoginRequest("john@example.com", "");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testLoginMissingEmail() throws Exception {
    LoginRequest request = new LoginRequest("", "password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testRefreshTokenSuccess() throws Exception {
    RefreshTokenRequest request = new RefreshTokenRequest("refresh-token");

    when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authResponse);

    mockMvc
        .perform(
            post("/api/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("jwt-token"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token"));

    verify(authService).refreshToken(any(RefreshTokenRequest.class));
  }

  @Test
  void testRefreshTokenMissingToken() throws Exception {
    RefreshTokenRequest request = new RefreshTokenRequest("");

    mockMvc
        .perform(
            post("/api/v1/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testLogoutSuccess() throws Exception {
    LogoutRequest request = new LogoutRequest("refresh-token");

    mockMvc
        .perform(
            post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Logout successful"));

    verify(authService).logout(any(LogoutRequest.class));
  }

  @Test
  void testLogoutMissingToken() throws Exception {
    LogoutRequest request = new LogoutRequest("");

    mockMvc
        .perform(
            post("/api/v1/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testRevokeTokenSuccess() throws Exception {
    RevokeTokenRequest request = new RevokeTokenRequest("refresh-token");

    mockMvc
        .perform(
            post("/api/v1/auth/revoke-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Token revoked successfully"));

    verify(authService).revokeToken(any(RevokeTokenRequest.class));
  }

  @Test
  void testRevokeTokenMissingToken() throws Exception {
    RevokeTokenRequest request = new RevokeTokenRequest("");

    mockMvc
        .perform(
            post("/api/v1/auth/revoke-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testChangePasswordSuccess() throws Exception {
    ChangePasswordRequest request =
        new ChangePasswordRequest("oldPassword", "newPassword123", "newPassword123");

    mockMvc
        .perform(
            post("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Password changed successfully"));

    verify(authService).changePassword(any(ChangePasswordRequest.class));
  }

  @Test
  void testChangePasswordMissingCurrentPassword() throws Exception {
    ChangePasswordRequest request =
        new ChangePasswordRequest("", "newPassword123", "newPassword123");

    mockMvc
        .perform(
            post("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testChangePasswordMissingNewPassword() throws Exception {
    ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "", "newPassword123");

    mockMvc
        .perform(
            post("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testChangePasswordMissingConfirmPassword() throws Exception {
    ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword123", "");

    mockMvc
        .perform(
            post("/api/v1/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testForgotPasswordSuccess() throws Exception {
    ForgotPasswordRequest request = new ForgotPasswordRequest("john@example.com");

    mockMvc
        .perform(
            post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Password reset link sent successfully"));

    verify(authService).forgotPassword(any(ForgotPasswordRequest.class));
  }

  @Test
  void testForgotPasswordInvalidEmail() throws Exception {
    ForgotPasswordRequest request = new ForgotPasswordRequest("invalid-email");

    mockMvc
        .perform(
            post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testForgotPasswordMissingEmail() throws Exception {
    ForgotPasswordRequest request = new ForgotPasswordRequest("");

    mockMvc
        .perform(
            post("/api/v1/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testResetPasswordSuccess() throws Exception {
    ResetPasswordRequest request =
        new ResetPasswordRequest("reset-token", "newPassword123", "newPassword123");

    mockMvc
        .perform(
            post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Password reset successfully"));

    verify(authService).resetPassword(any(ResetPasswordRequest.class));
  }

  @Test
  void testResetPasswordMissingToken() throws Exception {
    ResetPasswordRequest request = new ResetPasswordRequest("", "newPassword123", "newPassword123");

    mockMvc
        .perform(
            post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testResetPasswordMissingNewPassword() throws Exception {
    ResetPasswordRequest request = new ResetPasswordRequest("reset-token", "", "newPassword123");

    mockMvc
        .perform(
            post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testResetPasswordMissingConfirmPassword() throws Exception {
    ResetPasswordRequest request = new ResetPasswordRequest("reset-token", "newPassword123", "");

    mockMvc
        .perform(
            post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testSendOtpSuccess() throws Exception {
    SendOtpRequest request = new SendOtpRequest("john@example.com");

    mockMvc
        .perform(
            post("/api/v1/auth/send-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("OTP sent successfully"));

    verify(authService).sendOtp(any(SendOtpRequest.class));
  }

  @Test
  void testSendOtpInvalidEmail() throws Exception {
    SendOtpRequest request = new SendOtpRequest("invalid-email");

    mockMvc
        .perform(
            post("/api/v1/auth/send-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testSendOtpMissingEmail() throws Exception {
    SendOtpRequest request = new SendOtpRequest("");

    mockMvc
        .perform(
            post("/api/v1/auth/send-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testVerifyOtpSuccess() throws Exception {
    VerifyOtpRequest request = new VerifyOtpRequest("john@example.com", "123456");

    mockMvc
        .perform(
            post("/api/v1/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("OTP verified successfully"));

    verify(authService).verifyOtp(any(VerifyOtpRequest.class));
  }

  @Test
  void testVerifyOtpInvalidEmail() throws Exception {
    VerifyOtpRequest request = new VerifyOtpRequest("invalid-email", "123456");

    mockMvc
        .perform(
            post("/api/v1/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testVerifyOtpMissingEmail() throws Exception {
    VerifyOtpRequest request = new VerifyOtpRequest("", "123456");

    mockMvc
        .perform(
            post("/api/v1/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void testVerifyOtpMissingOtp() throws Exception {
    VerifyOtpRequest request = new VerifyOtpRequest("john@example.com", "");

    mockMvc
        .perform(
            post("/api/v1/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }
}

package com.digitalpayment.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import com.digitalpayment.auth.dto.AuthResponse;
import com.digitalpayment.auth.dto.ChangePasswordRequest;
import com.digitalpayment.auth.dto.ForgotPasswordRequest;
import com.digitalpayment.auth.dto.LoginRequest;
import com.digitalpayment.auth.dto.LogoutRequest;
import com.digitalpayment.auth.dto.RefreshTokenRequest;
import com.digitalpayment.auth.dto.RegisterRequest;
import com.digitalpayment.auth.dto.ResetPasswordRequest;
import com.digitalpayment.auth.dto.RevokeTokenRequest;
import org.junit.jupiter.api.Test;

class IAuthServiceTest {

  @Test
  void testIAuthServiceInterfaceExists() {
    assertNotNull(IAuthService.class);
    assertTrue(IAuthService.class.isInterface());
  }

  @Test
  void testIAuthServiceHasRegisterMethod() throws NoSuchMethodException {
    assertNotNull(IAuthService.class.getMethod("register", RegisterRequest.class));
  }

  @Test
  void testIAuthServiceHasLoginMethod() throws NoSuchMethodException {
    assertNotNull(IAuthService.class.getMethod("login", LoginRequest.class));
  }

  @Test
  void testIAuthServiceHasRefreshTokenMethod() throws NoSuchMethodException {
    assertNotNull(IAuthService.class.getMethod("refreshToken", RefreshTokenRequest.class));
  }

  @Test
  void testIAuthServiceHasLogoutMethod() throws NoSuchMethodException {
    assertNotNull(IAuthService.class.getMethod("logout", LogoutRequest.class));
  }

  @Test
  void testIAuthServiceHasRevokeTokenMethod() throws NoSuchMethodException {
    assertNotNull(IAuthService.class.getMethod("revokeToken", RevokeTokenRequest.class));
  }

  @Test
  void testIAuthServiceHasChangePasswordMethod() throws NoSuchMethodException {
    assertNotNull(IAuthService.class.getMethod("changePassword", ChangePasswordRequest.class));
  }

  @Test
  void testIAuthServiceHasForgotPasswordMethod() throws NoSuchMethodException {
    assertNotNull(IAuthService.class.getMethod("forgotPassword", ForgotPasswordRequest.class));
  }

  @Test
  void testIAuthServiceHasResetPasswordMethod() throws NoSuchMethodException {
    assertNotNull(IAuthService.class.getMethod("resetPassword", ResetPasswordRequest.class));
  }

  @Test
  void testIAuthServiceRegisterReturnsAuthResponse() throws NoSuchMethodException {
    var method = IAuthService.class.getMethod("register", RegisterRequest.class);
    assertEquals(AuthResponse.class, method.getReturnType());
  }

  @Test
  void testIAuthServiceLoginReturnsAuthResponse() throws NoSuchMethodException {
    var method = IAuthService.class.getMethod("login", LoginRequest.class);
    assertEquals(AuthResponse.class, method.getReturnType());
  }

  @Test
  void testIAuthServiceRefreshTokenReturnsAuthResponse() throws NoSuchMethodException {
    var method = IAuthService.class.getMethod("refreshToken", RefreshTokenRequest.class);
    assertEquals(AuthResponse.class, method.getReturnType());
  }

  @Test
  void testIAuthServiceLogoutReturnsVoid() throws NoSuchMethodException {
    var method = IAuthService.class.getMethod("logout", LogoutRequest.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  void testIAuthServiceRevokeTokenReturnsVoid() throws NoSuchMethodException {
    var method = IAuthService.class.getMethod("revokeToken", RevokeTokenRequest.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  void testIAuthServiceChangePasswordReturnsVoid() throws NoSuchMethodException {
    var method = IAuthService.class.getMethod("changePassword", ChangePasswordRequest.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  void testIAuthServiceForgotPasswordReturnsVoid() throws NoSuchMethodException {
    var method = IAuthService.class.getMethod("forgotPassword", ForgotPasswordRequest.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  void testIAuthServiceResetPasswordReturnsVoid() throws NoSuchMethodException {
    var method = IAuthService.class.getMethod("resetPassword", ResetPasswordRequest.class);
    assertEquals(void.class, method.getReturnType());
  }
}

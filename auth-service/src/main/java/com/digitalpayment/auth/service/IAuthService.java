package com.digitalpayment.auth.service;

import com.digitalpayment.auth.dto.*;
import jakarta.validation.Valid;

public interface IAuthService {

  AuthResponse register(@Valid RegisterRequest request);

  AuthResponse login(@Valid LoginRequest request);

  AuthResponse refreshToken(@Valid RefreshTokenRequest request);

  void logout(@Valid LogoutRequest request);

  void revokeToken(@Valid RevokeTokenRequest request);

  void changePassword(@Valid ChangePasswordRequest request);

  void forgotPassword(@Valid ForgotPasswordRequest request);

  void resetPassword(@Valid ResetPasswordRequest request);

  void sendOtp(@Valid SendOtpRequest request);

  void verifyOtp(VerifyOtpRequest request);
}

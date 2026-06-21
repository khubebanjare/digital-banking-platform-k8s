package com.digitalpayment.auth.service;

import com.digitalpayment.auth.dto.*;
import jakarta.validation.Valid;
import java.util.List;

public interface IAuthService {

  AuthResponse register(@Valid RegisterRequest request);

  void verifyEmail(String tokenValue);

  AuthResponse login(@Valid LoginRequest request);

  AuthResponse refreshToken(@Valid RefreshTokenRequest request);

  void logout(@Valid LogoutRequest request);

  void revokeToken(@Valid RevokeTokenRequest request);

  void changePassword(@Valid ChangePasswordRequest request);

  void forgotPassword(@Valid ForgotPasswordRequest request);

  void resetPassword(@Valid ResetPasswordRequest request);

  void sendOtp(@Valid SendOtpRequest request);

  void verifyOtp(VerifyOtpRequest request);

  void enableMfa(EnableMfaRequest request);

  void disableMfa(DisableMfaRequest request);

  List<SessionResponse> getSessions(String email);

  void deleteSession(Long sessionId, String email);

  void sendLoginOtp(SendLoginOtpRequest request);

  AuthResponse verifyLoginOtp(VerifyLoginOtpRequest request);

  void resendLoginOtp(ResendLoginOtpRequest request);
}

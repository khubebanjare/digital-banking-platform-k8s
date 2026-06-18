package com.digitalpayment.auth.controller;

import com.digitalpayment.auth.dto.*;
import com.digitalpayment.auth.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication and registration endpoints")
public class AuthController {

  private final IAuthService authService;

  @PostMapping("/register")
  @Operation(
      summary = "Register a new user",
      description = "Creates a new user account and returns access and refresh tokens")
  @ApiResponse(responseCode = "200", description = "User registered successfully")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  @ApiResponse(responseCode = "409", description = "Email already exists")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    log.info("Registration request received for email: {}", request.getEmail());
    AuthResponse response = authService.register(request);
    log.info("Registration successful for email: {}", request.getEmail());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  @Operation(
      summary = "Authenticate user",
      description = "Validates user credentials and returns access and refresh tokens")
  @ApiResponse(responseCode = "200", description = "Login successful")
  @ApiResponse(responseCode = "401", description = "Invalid credentials")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    log.info("Login request received for email: {}", request.getEmail());
    AuthResponse response = authService.login(request);
    log.info("Login successful for email: {}", request.getEmail());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh-token")
  @Operation(
      summary = "Refresh access token",
      description = "Generates a new access token using a valid refresh token")
  @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
  @ApiResponse(responseCode = "401", description = "Refresh token expired or invalid")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  public ResponseEntity<AuthResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest request) {
    log.info("Refresh token request received");
    AuthResponse response = authService.refreshToken(request);
    log.info("Refresh token successful");
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  @Operation(
      summary = "Logout user",
      description = "Invalidates the refresh token and revokes access to the API")
  @ApiResponse(responseCode = "200", description = "Logout successful")
  @ApiResponse(responseCode = "401", description = "Invalid credentials")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  public ResponseEntity<MessageResponse> logout(@Valid @RequestBody LogoutRequest request) {
    log.info("Logout request received");
    authService.logout(request);
    log.info("Logout successful");
    return ResponseEntity.ok(new MessageResponse("Logout successful"));
  }

  @PostMapping("/revoke-token")
  @Operation(
      summary = "Revoke refresh token",
      description =
          "Revokes the refresh token, preventing further use for obtaining new access tokens")
  @ApiResponse(responseCode = "200", description = "Token revoked successfully")
  @ApiResponse(responseCode = "401", description = "Invalid credentials")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  public ResponseEntity<MessageResponse> revokeToken(
      @Valid @RequestBody RevokeTokenRequest request) {

    authService.revokeToken(request);

    return ResponseEntity.ok(new MessageResponse("Token revoked successfully"));
  }

  @PostMapping("/change-password")
  @Operation(summary = "Change user password", description = "Changes the user's password")
  @ApiResponse(responseCode = "200", description = "Password changed successfully")
  @ApiResponse(responseCode = "401", description = "Invalid credentials")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  public ResponseEntity<MessageResponse> changePassword(
      @Valid @RequestBody ChangePasswordRequest request) {
    authService.changePassword(request);

    return ResponseEntity.ok(new MessageResponse("Password changed successfully"));
  }

  @PostMapping("/forgot-password")
  @Operation(
      summary = "Send password reset link",
      description = "Sends a password reset link to the user's email")
  @ApiResponse(responseCode = "200", description = "Password reset link sent successfully")
  @ApiResponse(responseCode = "401", description = "Invalid credentials")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  public ResponseEntity<MessageResponse> forgotPassword(
      @Valid @RequestBody ForgotPasswordRequest request) {

    log.info("Password reset requested for email: {}", request.email());

    authService.forgotPassword(request);

    return ResponseEntity.ok(new MessageResponse("Password reset link sent successfully"));
  }

  @PostMapping("/reset-password")
  @Operation(
      summary = "Reset password using reset token",
      description = "Resets the user's password using a reset token")
  @ApiResponse(responseCode = "200", description = "Password reset successfully")
  @ApiResponse(responseCode = "401", description = "Invalid credentials")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  public ResponseEntity<MessageResponse> resetPassword(
      @Valid @RequestBody ResetPasswordRequest request) {
    log.info("Reset password request received");
    authService.resetPassword(request);

    return ResponseEntity.ok(new MessageResponse("Password reset successfully"));
  }

  @PostMapping("/send-otp")
  @Operation(
      summary = "Send OTP to email",
      description = "Sends a One-Time Password (OTP) to the user's email")
  @ApiResponse(responseCode = "200", description = "OTP sent successfully")
  @ApiResponse(responseCode = "401", description = "Invalid credentials")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  public ResponseEntity<MessageResponse> sendOtp(@RequestBody SendOtpRequest request) {
    log.info("Send OTP request received for email: {}", request.email());
    authService.sendOtp(request);

    return ResponseEntity.ok(new MessageResponse("OTP sent successfully"));
  }

  @PostMapping("/verify-otp")
  @Operation(summary = "Verify OTP", description = "Verifies the One-Time Password (OTP)")
  @ApiResponse(responseCode = "200", description = "OTP verified successfully")
  @ApiResponse(responseCode = "401", description = "Invalid credentials")
  @ApiResponse(responseCode = "400", description = "Invalid request")
  public ResponseEntity<MessageResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
    log.info("Verify OTP request received for email: {}", request.email());
    authService.verifyOtp(request);

    return ResponseEntity.ok(new MessageResponse("OTP verified successfully"));
  }
}

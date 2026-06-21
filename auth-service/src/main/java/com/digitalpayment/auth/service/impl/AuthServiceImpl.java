package com.digitalpayment.auth.service.impl;

import com.digitalpayment.auth.config.JwtProperties;
import com.digitalpayment.auth.constants.AuthConstants;
import com.digitalpayment.auth.dto.*;
import com.digitalpayment.auth.entity.*;
import com.digitalpayment.auth.exception.*;
import com.digitalpayment.auth.metrics.AuthMetrics;
import com.digitalpayment.auth.repository.*;
import com.digitalpayment.auth.service.EmailService;
import com.digitalpayment.auth.service.IAuthService;
import com.digitalpayment.auth.service.RefreshTokenService;
import com.digitalpayment.auth.util.JwtUtil;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;
  private final AuthMetrics authMetrics;
  private final ObservationRegistry observationRegistry;
  private final RefreshTokenService refreshTokenService;
  private final JwtProperties jwtProperties;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  private final EmailService emailService;
  private final OtpTokenRepository otpTokenRepository;
  private final EmailVerificationTokenRepository emailVerificationTokenRepository;

  @Override
  @Transactional
  public AuthResponse register(RegisterRequest request) {
    return Observation.createNotStarted("auth.register", observationRegistry)
        .observe(
            () -> {
              if (userRepository.existsByEmail(request.getEmail())) {
                log.warn("Registration attempt with existing email: {}", request.getEmail());
                throw new DuplicateEmailException(
                    "Email already registered: " + request.getEmail());
              }

              User user = new User();
              user.setFirstName(request.getFirstName());
              user.setLastName(request.getLastName());
              user.setEmail(request.getEmail());
              user.setPassword(passwordEncoder.encode(request.getPassword()));

              user = userRepository.save(user);

              String verificationToken = UUID.randomUUID().toString();

              emailVerificationTokenRepository.deleteByUser(user);

              EmailVerificationToken emailToken =
                  EmailVerificationToken.builder()
                      .token(verificationToken)
                      .expiryDate(Instant.now().plus(24, ChronoUnit.HOURS))
                      .user(user)
                      .build();

              emailVerificationTokenRepository.save(emailToken);

              String verificationLink =
                  "http://localhost:8081/api/v1/auth/verify-email?token=" + verificationToken;

              try {
                emailService.sendVerificationEmail(user.getEmail(), verificationLink);

              } catch (MessagingException | UnsupportedEncodingException e) {
                throw new EmailSendingException("Failed to send verification email", e);
              }

              authMetrics.getRegistrationSuccessCounter().increment();

              log.info("User registered successfully with ID: {}", user.getId());

              String accessToken = jwtUtil.generateToken(user.getEmail());

              RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

              return new AuthResponse(
                  accessToken,
                  refreshToken.getToken(),
                  AuthConstants.TOKEN_PREFIX,
                  jwtProperties.getExpiration() / 1000,
                  user.getId(),
                  user.getEmail(),
                  user.getRole());
            });
  }

  @Override
  @Transactional
  public void verifyEmail(String tokenValue) {
    log.info("Email verification request received for token: {}", tokenValue);
    EmailVerificationToken token =
        emailVerificationTokenRepository
            .findByToken(tokenValue)
            .orElseThrow(() -> new AuthenticationException("Invalid verification token"));

    if (token.getExpiryDate().isBefore(Instant.now())) {

      throw new AuthenticationException("Verification token expired");
    }

    User user = token.getUser();

    user.setEmailVerified(true);

    user.setEnabled(true);

    userRepository.save(user);

    emailVerificationTokenRepository.delete(token);
    log.info("Email verified successfully for user: {}", user.getEmail());
  }

  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    return Observation.createNotStarted("auth.login", observationRegistry)
        .observe(
            () -> {
              try {

                log.info("Login attempt for email: {}", request.getEmail());

                Authentication authentication =
                    authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

                User user = (User) authentication.getPrincipal();

                if (user == null) {
                  authMetrics.getLoginFailureCounter().increment();

                  throw new InvalidCredentialsException("Invalid credentials");
                }

                User currentUser = userRepository.findByEmail(request.getEmail()).orElseThrow();

                String accessToken = jwtUtil.generateToken(currentUser.getEmail());

                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

                authMetrics.getLoginSuccessCounter().increment();

                log.info("User logged in successfully with email: {}", user.getEmail());

                return new AuthResponse(
                    accessToken,
                    refreshToken.getToken(),
                    AuthConstants.TOKEN_PREFIX,
                    jwtProperties.getExpiration() / 1000,
                    user.getId(),
                    user.getEmail(),
                    user.getRole());

              } catch (org.springframework.security.authentication.BadCredentialsException e) {

                authMetrics.getLoginFailureCounter().increment();

                log.warn("Failed login attempt for email: {}", request.getEmail());

                throw new InvalidCredentialsException("Invalid email or password");

              } catch (Exception e) {

                authMetrics.getLoginFailureCounter().increment();

                log.error("Unexpected error during login for email: {}", request.getEmail(), e);

                throw new AuthenticationException("Login failed due to server error", e);
              }
            });
  }

  @Transactional
  @Override
  public AuthResponse refreshToken(RefreshTokenRequest request) {

    log.info("Refresh token request received");

    RefreshToken refreshToken =
        refreshTokenService
            .findByToken(request.refreshToken())
            .map(refreshTokenService::verifyExpiration)
            .orElseThrow(() -> new AuthenticationException("Invalid refresh token"));

    User user = refreshToken.getUser();

    String accessToken = jwtUtil.generateToken(user.getEmail());

    log.info("Access token refreshed successfully for user: {}", user.getEmail());

    return new AuthResponse(
        accessToken,
        refreshToken.getToken(),
        AuthConstants.TOKEN_PREFIX,
        86400L,
        user.getId(),
        user.getEmail(),
        user.getRole());
  }

  @Override
  @Transactional
  public void logout(LogoutRequest request) {
    log.info("Logout request received");
    RefreshToken existingToken =
        refreshTokenService
            .findByToken(request.refreshToken())
            .orElseThrow(() -> new AuthenticationException("Invalid refresh token"));
    log.info("Deleting refresh token for user: {}", existingToken.getUser().getEmail());
    refreshTokenRepository.delete(existingToken);
  }

  @Transactional
  @Override
  public void revokeToken(RevokeTokenRequest request) {
    log.info("Revoke token request received");
    refreshTokenService.revokeToken(request.refreshToken());
  }

  @Transactional
  @Override
  public void changePassword(ChangePasswordRequest request) {
    String email =
        Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
    log.info("Change password request received for user: {}", email);
    User user = getUser(email);

    if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
      throw new AuthenticationException("Current password is incorrect");
    }

    if (!request.newPassword().equals(request.confirmPassword())) {
      throw new AuthenticationException("Passwords do not match");
    }

    user.setPassword(passwordEncoder.encode(request.newPassword()));

    userRepository.save(user);

    refreshTokenService.deleteByUser(user);
    log.info("Password changed successfully for user: {}", email);
  }

  @Override
  @Transactional
  public void forgotPassword(ForgotPasswordRequest request) {
    log.info("Forgot password request received for email: {}", request.email());
    User user = getUser(request.email());

    passwordResetTokenRepository.deleteByUser(user);
    passwordResetTokenRepository.flush();

    String token = UUID.randomUUID().toString();

    PasswordResetToken resetToken =
        PasswordResetToken.builder()
            .token(token)
            .expiryDate(Instant.now().plusSeconds(900))
            .user(user)
            .build();

    PasswordResetToken saved = passwordResetTokenRepository.save(resetToken);

    log.info("Password reset token saved: {}", saved.getToken());
    String resetLink = "http://localhost:8081/api/v1/auth/reset-password?token=" + token;

    try {
      emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    } catch (MessagingException | UnsupportedEncodingException e) {
      log.error("Failed to send password reset email to: {}", user.getEmail(), e);
      throw new EmailSendingException("Failed to send password reset email", e);
    }
    log.info("Password reset email sent successfully to: {}", user.getEmail());
  }

  @Override
  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
    log.info("Reset password request received for token: {}", request.token());
    PasswordResetToken resetToken =
        passwordResetTokenRepository
            .findByToken(request.token())
            .orElseThrow(() -> new AuthenticationException("Invalid token"));

    if (resetToken.getExpiryDate().isBefore(Instant.now())) {
      passwordResetTokenRepository.delete(resetToken);
      throw new AuthenticationException("Token expired");
    }

    if (!request.newPassword().equals(request.confirmPassword())) {
      throw new AuthenticationException("Passwords do not match");
    }

    User user = resetToken.getUser();

    user.setPassword(passwordEncoder.encode(request.newPassword()));

    userRepository.save(user);

    passwordResetTokenRepository.delete(resetToken);

    refreshTokenService.deleteByUser(user);
    log.info("Password reset successfully for user: {}", user.getEmail());
  }

  @Override
  @Transactional
  public void sendOtp(SendOtpRequest request) {
    log.info("Send OTP request received for email: {}", request.email());
    User user = getUser(request.email());

    otpTokenRepository.deleteByUser(user);

    String otp = generateOtp();

    OtpToken otpToken =
        OtpToken.builder()
            .otp(otp)
            .verified(false)
            .expiryDate(Instant.now().plusSeconds(300))
            .user(user)
            .build();

    otpTokenRepository.save(otpToken);

    sendOptEmail(user, otp);
  }

  @Override
  @Transactional
  public void verifyOtp(VerifyOtpRequest request) {
    log.info("Verify OTP request received for email: {}", request.email());
    User user = getUser(request.email());

    OtpToken otpToken = getOtpToken(user);

    if (otpToken.getExpiryDate().isBefore(Instant.now())) {
      otpTokenRepository.delete(otpToken);
      throw new AuthenticationException("OTP expired");
    }

    if (!otpToken.getOtp().equals(request.otp())) {
      throw new AuthenticationException("Invalid OTP");
    }

    otpToken.setVerified(true);

    otpTokenRepository.save(otpToken);
    log.info("OTP verified successfully for user: {}", user.getEmail());
  }

  @Override
  @Transactional
  public void enableMfa(EnableMfaRequest request) {
    log.info("Enable MFA request received for email: {}", request.email());
    User user = getUser(request.email());

    OtpToken otpToken = getOtpToken(user);

    if (otpToken.getExpiryDate().isBefore(Instant.now())) {
      throw new AuthenticationException("OTP expired");
    }

    if (!otpToken.getOtp().equals(request.otp())) {
      throw new AuthenticationException("Invalid OTP");
    }

    user.setMfaEnabled(true);

    userRepository.save(user);

    otpTokenRepository.delete(otpToken);
    log.info("MFA enabled successfully for user: {}", user.getEmail());
  }

  @Override
  @Transactional
  public void disableMfa(DisableMfaRequest request) {
    log.info("Disable MFA request received for email: {}", request.email());
    User user = getUser(request.email());

    OtpToken otpToken = getOtpToken(user);

    if (otpToken.getExpiryDate().isBefore(Instant.now())) {
      throw new AuthenticationException("OTP expired");
    }

    if (!otpToken.getOtp().equals(request.otp())) {
      throw new AuthenticationException("Invalid OTP");
    }

    user.setMfaEnabled(false);

    userRepository.save(user);

    otpTokenRepository.delete(otpToken);
    log.info("MFA disabled successfully for user: {}", user.getEmail());
  }

  @Override
  @Transactional(readOnly = true)
  public List<SessionResponse> getSessions(String email) {
    log.info("Get sessions request received for email: {}", email);
    User user = getUser(email);

    List<SessionResponse> sessions =
        refreshTokenRepository.findAllByUser(user).stream()
            .map(
                token ->
                    new SessionResponse(token.getId(), token.getToken(), token.getExpiryDate()))
            .toList();
    log.info("Retrieved {} sessions for user: {}", sessions.size(), email);
    return sessions;
  }

  @Override
  @Transactional
  public void deleteSession(Long sessionId, String email) {
    log.info("Delete session request received for session ID: {} and email: {}", sessionId, email);
    User user = getUser(email);

    RefreshToken refreshToken =
        refreshTokenRepository
            .findById(sessionId)
            .orElseThrow(() -> new AuthenticationException("Session not found"));

    if (!refreshToken.getUser().getId().equals(user.getId())) {
      throw new AuthenticationException("Unauthorized session access");
    }

    refreshTokenRepository.delete(refreshToken);
    log.info("Session deleted successfully for session ID: {}", sessionId);
  }

  @Override
  @Transactional
  public void sendLoginOtp(SendLoginOtpRequest request) {
    User user = getUser(request.email());
    if (!user.isEnabled()) {
      throw new AuthenticationException("Email is not verified");
    }

    otpTokenRepository.deleteByUser(user);

    String otp = generateOtp();

    OtpToken otpToken = generateOtpToken(user, otp);

    otpTokenRepository.save(otpToken);

    sendOptEmail(user, otp);
  }

  @Override
  @Transactional
  public AuthResponse verifyLoginOtp(VerifyLoginOtpRequest request) {
    User user = getUser(request.email());

    OtpToken otpToken = getOtpToken(user);

    if (otpToken.getExpiryDate().isBefore(Instant.now())) {
      throw new AuthenticationException("OTP expired");
    }

    if (!otpToken.getOtp().equals(request.otp())) {
      throw new AuthenticationException("Invalid OTP");
    }

    otpTokenRepository.delete(otpToken);

    String accessToken = jwtUtil.generateToken(user.getEmail());

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

    return new AuthResponse(
        accessToken,
        refreshToken.getToken(),
        "Bearer",
        jwtProperties.getExpiration(),
        user.getId(),
        user.getEmail(),
        user.getRole());
  }

  @Override
  @Transactional
  public void resendLoginOtp(ResendLoginOtpRequest request) {
    User user = getUser(request.email());

    if (!user.isEnabled()) {
      throw new AuthenticationException("Email is not verified");
    }

    otpTokenRepository.deleteByUser(user);
    String otp = generateOtp();
    OtpToken otpToken = generateOtpToken(user, otp);
    otpTokenRepository.save(otpToken);
    sendOptEmail(user, otp);
  }

  private String generateOtp() {
    return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
  }

  private OtpToken generateOtpToken(User user, String otp) {
    return OtpToken.builder()
        .otp(otp)
        .user(user)
        .expiryDate(Instant.now().plus(5, ChronoUnit.MINUTES))
        .build();
  }

  private OtpToken getOtpToken(User user) {
    return otpTokenRepository
        .findByUser(user)
        .orElseThrow(() -> new AuthenticationException("OTP not found"));
  }

  private User getUser(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new AuthenticationException(AuthConstants.USER_NOT_FOUND));
  }

  private void sendOptEmail(User user, String otp) {
    try {
      emailService.sendOtpEmail(user.getEmail(), otp);
    } catch (MessagingException | UnsupportedEncodingException e) {
      throw new EmailSendingException("Failed to send OTP email", e);
    }
    log.info("OTP sent successfully to: {}", user.getEmail());
  }
}

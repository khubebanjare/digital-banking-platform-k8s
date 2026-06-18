package com.digitalpayment.auth.service.impl;

import com.digitalpayment.auth.config.JwtProperties;
import com.digitalpayment.auth.constants.AuthConstants;
import com.digitalpayment.auth.dto.*;
import com.digitalpayment.auth.entity.OtpToken;
import com.digitalpayment.auth.entity.PasswordResetToken;
import com.digitalpayment.auth.entity.RefreshToken;
import com.digitalpayment.auth.entity.User;
import com.digitalpayment.auth.exception.*;
import com.digitalpayment.auth.metrics.AuthMetrics;
import com.digitalpayment.auth.repository.OtpTokenRepository;
import com.digitalpayment.auth.repository.PasswordResetTokenRepository;
import com.digitalpayment.auth.repository.RefreshTokenRepository;
import com.digitalpayment.auth.repository.UserRepository;
import com.digitalpayment.auth.service.EmailService;
import com.digitalpayment.auth.service.IAuthService;
import com.digitalpayment.auth.service.RefreshTokenService;
import com.digitalpayment.auth.util.JwtUtil;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.mail.MessagingException;
import java.time.Instant;
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
    log.info("Change password request received");
    String email =
        Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new AuthenticationException(AuthConstants.USER_NOT_FOUND));

    if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {

      throw new AuthenticationException("Current password is incorrect");
    }

    if (!request.newPassword().equals(request.confirmPassword())) {

      throw new AuthenticationException("Passwords do not match");
    }

    user.setPassword(passwordEncoder.encode(request.newPassword()));

    userRepository.save(user);

    refreshTokenService.deleteByUser(user);
  }

  @Override
  @Transactional
  public void forgotPassword(ForgotPasswordRequest request) {

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new AuthenticationException(AuthConstants.USER_NOT_FOUND));

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

    } catch (MessagingException e) {

      throw new EmailSendingException("Failed to send password reset email", e);
    }
  }

  @Override
  @Transactional
  public void resetPassword(ResetPasswordRequest request) {
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
  }

  @Override
  @Transactional
  public void sendOtp(SendOtpRequest request) {

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new AuthenticationException(AuthConstants.USER_NOT_FOUND));

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

    try {
      emailService.sendOtpEmail(user.getEmail(), otp);
    } catch (MessagingException e) {
      throw new OtpEmailSendingException("Failed to send OTP email", e);
    }
  }

  @Override
  @Transactional
  public void verifyOtp(VerifyOtpRequest request) {

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new AuthenticationException(AuthConstants.USER_NOT_FOUND));

    OtpToken otpToken =
        otpTokenRepository
            .findByUser(user)
            .orElseThrow(() -> new AuthenticationException("OTP not found"));

    if (otpToken.getExpiryDate().isBefore(Instant.now())) {

      otpTokenRepository.delete(otpToken);

      throw new AuthenticationException("OTP expired");
    }

    if (!otpToken.getOtp().equals(request.otp())) {

      throw new AuthenticationException("Invalid OTP");
    }

    otpToken.setVerified(true);

    otpTokenRepository.save(otpToken);
  }

  private String generateOtp() {

    return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
  }
}

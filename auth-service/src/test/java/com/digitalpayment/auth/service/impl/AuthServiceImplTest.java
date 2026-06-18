package com.digitalpayment.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.digitalpayment.auth.config.JwtProperties;
import com.digitalpayment.auth.dto.*;
import com.digitalpayment.auth.entity.*;
import com.digitalpayment.auth.exception.*;
import com.digitalpayment.auth.metrics.AuthMetrics;
import com.digitalpayment.auth.repository.OtpTokenRepository;
import com.digitalpayment.auth.repository.PasswordResetTokenRepository;
import com.digitalpayment.auth.repository.RefreshTokenRepository;
import com.digitalpayment.auth.repository.UserRepository;
import com.digitalpayment.auth.service.EmailService;
import com.digitalpayment.auth.service.RefreshTokenService;
import com.digitalpayment.auth.util.JwtUtil;
import io.micrometer.core.instrument.Counter;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.mail.MessagingException;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceImplTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtUtil jwtUtil;

  @Mock private AuthenticationManager authenticationManager;

  @Mock private AuthMetrics authMetrics;

  @Mock private Counter loginSuccessCounter;

  @Mock private Counter loginFailureCounter;

  @Mock private Counter registrationSuccessCounter;

  @Mock private RefreshTokenService refreshTokenService;

  @Mock private JwtProperties jwtProperties;

  @Mock private RefreshTokenRepository refreshTokenRepository;

  @Mock private PasswordResetTokenRepository passwordResetTokenRepository;

  @Mock private EmailService emailService;

  @Mock private OtpTokenRepository otpTokenRepository;

  @Mock private ObservationRegistry observationRegistry;

  @InjectMocks private AuthServiceImpl authService;

  private MockedStatic<Observation> mockedObservation;

  private RegisterRequest registerRequest;
  private LoginRequest loginRequest;
  private User user;
  private RefreshToken refreshToken;

  @BeforeEach
  void setUp() {
    UUID userId = UUID.randomUUID();

    registerRequest = new RegisterRequest("John", "Doe", "john@example.com", "password123");
    loginRequest = new LoginRequest("john@example.com", "password123");

    user = new User();
    user.setId(userId);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john@example.com");
    user.setPassword("encodedPassword");
    user.setRole(Role.USER);
    user.setEnabled(true);
    user.setAccountNonExpired(true);
    user.setAccountNonLocked(true);
    user.setCredentialsNonExpired(true);

    refreshToken = new RefreshToken();
    refreshToken.setId(1L);
    refreshToken.setToken("refresh-token-123");
    refreshToken.setUser(user);

    when(authMetrics.getLoginSuccessCounter()).thenReturn(loginSuccessCounter);
    when(authMetrics.getLoginFailureCounter()).thenReturn(loginFailureCounter);
    when(authMetrics.getRegistrationSuccessCounter()).thenReturn(registrationSuccessCounter);
    when(jwtProperties.getExpiration()).thenReturn(86400000L);

    // Mock Observation.createNotStarted to execute lambdas directly
    mockedObservation = mockStatic(Observation.class);
    mockedObservation
        .when(() -> Observation.createNotStarted(anyString(), any(ObservationRegistry.class)))
        .thenAnswer(
            invocation -> {
              Observation mockObs = mock(Observation.class);
              doAnswer(
                      obsInv -> {
                        Runnable runnable = obsInv.getArgument(0);
                        runnable.run();
                        return null;
                      })
                  .when(mockObs)
                  .observe(any(Runnable.class));
              doAnswer(
                      obsInv -> {
                        java.util.function.Supplier<?> supplier = obsInv.getArgument(0);
                        return supplier.get();
                      })
                  .when(mockObs)
                  .observe(any(java.util.function.Supplier.class));
              return mockObs;
            });
  }

  @AfterEach
  void tearDown() {
    if (mockedObservation != null) {
      mockedObservation.close();
    }
    SecurityContextHolder.clearContext();
  }

  @Test
  void testRegisterSuccess() {
    User savedUser = new User();
    savedUser.setId(user.getId());
    savedUser.setFirstName("John");
    savedUser.setLastName("Doe");
    savedUser.setEmail("john@example.com");
    savedUser.setPassword("encodedPassword");
    savedUser.setRole(Role.USER);
    savedUser.setEnabled(true);
    savedUser.setAccountNonExpired(true);
    savedUser.setAccountNonLocked(true);
    savedUser.setCredentialsNonExpired(true);

    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(jwtUtil.generateToken("john@example.com")).thenReturn("jwt-token");
    when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(refreshToken);

    AuthResponse response = authService.register(registerRequest);

    assertNotNull(response);
    assertEquals("jwt-token", response.accessToken());
    assertEquals("refresh-token-123", response.refreshToken());
    assertEquals("Bearer", response.tokenType());
    assertNotNull(response.expiresIn());
    assertEquals("john@example.com", response.email());
    assertEquals(Role.USER, response.role());

    verify(userRepository).existsByEmail("john@example.com");
    verify(passwordEncoder).encode("password123");

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());
    User capturedUser = userCaptor.getValue();
    assertEquals("john@example.com", capturedUser.getEmail());
    assertEquals("encodedPassword", capturedUser.getPassword());

    verify(jwtUtil).generateToken("john@example.com");
    verify(refreshTokenService).createRefreshToken(any(User.class));
    verify(registrationSuccessCounter).increment();
  }

  @Test
  void testRegisterDuplicateEmail() {
    when(userRepository.existsByEmail("john@example.com")).thenReturn(true);
    lenient()
        .when(authMetrics.getRegistrationSuccessCounter())
        .thenReturn(registrationSuccessCounter);

    assertThrows(DuplicateEmailException.class, () -> authService.register(registerRequest));

    verify(userRepository).existsByEmail("john@example.com");
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testLoginSuccess() {
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    when(authenticationManager.authenticate(any())).thenReturn(authentication);
    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    when(jwtUtil.generateToken("john@example.com")).thenReturn("jwt-token");
    when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(refreshToken);

    AuthResponse response = authService.login(loginRequest);

    assertNotNull(response);
    assertEquals("jwt-token", response.accessToken());
    assertEquals("refresh-token-123", response.refreshToken());
    assertEquals("Bearer", response.tokenType());
    assertNotNull(response.expiresIn());
    assertEquals("john@example.com", response.email());
    assertEquals(Role.USER, response.role());

    verify(authenticationManager).authenticate(any());
    verify(userRepository).findByEmail("john@example.com");
    verify(jwtUtil).generateToken("john@example.com");
    verify(refreshTokenService).createRefreshToken(any(User.class));
    verify(loginSuccessCounter).increment();
    verify(loginFailureCounter, never()).increment();
  }

  @Test
  void testLoginBadCredentials() {
    when(authenticationManager.authenticate(any()))
        .thenThrow(new BadCredentialsException("Invalid credentials"));

    assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));

    verify(authenticationManager).authenticate(any());
    verify(loginFailureCounter).increment();
    verify(loginSuccessCounter, never()).increment();
  }

  @Test
  void testLoginUserNotFound() {
    when(authenticationManager.authenticate(any()))
        .thenThrow(new UsernameNotFoundException("User not found"));

    assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));

    verify(authenticationManager).authenticate(any());
    verify(loginFailureCounter).increment();
    verify(loginSuccessCounter, never()).increment();
  }

  @Test
  void testLoginNullPrincipal() {
    Authentication authentication = new UsernamePasswordAuthenticationToken(null, null);
    when(authenticationManager.authenticate(any())).thenReturn(authentication);

    assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));

    verify(authenticationManager).authenticate(any());
    verify(loginFailureCounter, atLeastOnce()).increment();
    verify(loginSuccessCounter, never()).increment();
  }

  @Test
  void testRegisterWithSystemOutVerification() {
    User savedUser = new User();
    savedUser.setId(user.getId());
    savedUser.setFirstName("John");
    savedUser.setLastName("Doe");
    savedUser.setEmail("john@example.com");
    savedUser.setPassword("encodedPassword");
    savedUser.setRole(Role.USER);
    savedUser.setEnabled(true);
    savedUser.setAccountNonExpired(true);
    savedUser.setAccountNonLocked(true);
    savedUser.setCredentialsNonExpired(true);

    when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(jwtUtil.generateToken("john@example.com")).thenReturn("jwt-token");
    when(refreshTokenService.createRefreshToken(any(User.class))).thenReturn(refreshToken);

    AuthResponse response = authService.register(registerRequest);

    assertNotNull(response);
    assertEquals("jwt-token", response.accessToken());
    assertEquals("refresh-token-123", response.refreshToken());
    assertNotNull(response.expiresIn());
    verify(registrationSuccessCounter).increment();
  }

  @Test
  void testLoginUnexpectedException() {
    when(authenticationManager.authenticate(any()))
        .thenThrow(new RuntimeException("Unexpected error"));

    assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));

    verify(authenticationManager).authenticate(any());
    verify(loginFailureCounter).increment();
    verify(loginSuccessCounter, never()).increment();
  }

  @Test
  void testRefreshToken_Success() {
    RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
    when(refreshTokenService.findByToken("valid-refresh-token"))
        .thenReturn(java.util.Optional.of(refreshToken));
    when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
    when(jwtUtil.generateToken("john@example.com")).thenReturn("new-access-token");

    AuthResponse response = authService.refreshToken(request);

    assertNotNull(response);
    assertEquals("new-access-token", response.accessToken());
    assertEquals("refresh-token-123", response.refreshToken());
    assertEquals("Bearer", response.tokenType());
    assertEquals(86400L, response.expiresIn());
    assertEquals("john@example.com", response.email());
    assertEquals(Role.USER, response.role());

    verify(refreshTokenService).findByToken("valid-refresh-token");
    verify(refreshTokenService).verifyExpiration(refreshToken);
    verify(jwtUtil).generateToken("john@example.com");
  }

  @Test
  void testRefreshToken_InvalidToken() {
    RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
    when(refreshTokenService.findByToken("invalid-token")).thenReturn(java.util.Optional.empty());

    assertThrows(AuthenticationException.class, () -> authService.refreshToken(request));

    verify(refreshTokenService).findByToken("invalid-token");
    verify(refreshTokenService, never()).verifyExpiration(any(RefreshToken.class));
    verify(jwtUtil, never()).generateToken(anyString());
  }

  @Test
  void testRefreshToken_ExpiredToken() {
    RefreshTokenRequest request = new RefreshTokenRequest("expired-token");
    when(refreshTokenService.findByToken("expired-token"))
        .thenReturn(java.util.Optional.of(refreshToken));
    when(refreshTokenService.verifyExpiration(refreshToken))
        .thenThrow(new AuthenticationException("Refresh token expired"));

    assertThrows(AuthenticationException.class, () -> authService.refreshToken(request));

    verify(refreshTokenService).findByToken("expired-token");
    verify(refreshTokenService).verifyExpiration(refreshToken);
    verify(jwtUtil, never()).generateToken(anyString());
  }

  @Test
  void testRefreshToken_GeneratesNewAccessToken() {
    RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
    when(refreshTokenService.findByToken("valid-refresh-token"))
        .thenReturn(java.util.Optional.of(refreshToken));
    when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
    when(jwtUtil.generateToken("john@example.com")).thenReturn("new-access-token");

    AuthResponse response = authService.refreshToken(request);

    assertEquals("new-access-token", response.accessToken());
    assertNotEquals("old-access-token", response.accessToken());
    verify(jwtUtil).generateToken("john@example.com");
  }

  @Test
  void testRefreshToken_ReturnsSameRefreshToken() {
    RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
    when(refreshTokenService.findByToken("valid-refresh-token"))
        .thenReturn(java.util.Optional.of(refreshToken));
    when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
    when(jwtUtil.generateToken("john@example.com")).thenReturn("new-access-token");

    AuthResponse response = authService.refreshToken(request);

    assertEquals("refresh-token-123", response.refreshToken());
  }

  @Test
  void testLogoutSuccess() {
    LogoutRequest request = new LogoutRequest("refresh-token-123");
    when(refreshTokenService.findByToken("refresh-token-123"))
        .thenReturn(java.util.Optional.of(refreshToken));

    authService.logout(request);

    verify(refreshTokenService).findByToken("refresh-token-123");
    verify(refreshTokenRepository).delete(refreshToken);
  }

  @Test
  void testLogoutInvalidToken() {
    LogoutRequest request = new LogoutRequest("invalid-token");
    when(refreshTokenService.findByToken("invalid-token")).thenReturn(java.util.Optional.empty());

    assertThrows(AuthenticationException.class, () -> authService.logout(request));

    verify(refreshTokenService).findByToken("invalid-token");
    verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
  }

  @Test
  void testRevokeTokenSuccess() {
    RevokeTokenRequest request = new RevokeTokenRequest("refresh-token-123");

    authService.revokeToken(request);

    verify(refreshTokenService).revokeToken("refresh-token-123");
  }

  @Test
  void testChangePasswordSuccess() {
    ChangePasswordRequest request =
        new ChangePasswordRequest("oldPassword", "newPassword123", "newPassword123");

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
    when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);

    authService.changePassword(request);

    verify(passwordEncoder).matches("oldPassword", "encodedPassword");
    verify(passwordEncoder).encode("newPassword123");
    verify(userRepository).save(any(User.class));
    verify(refreshTokenService).deleteByUser(user);
  }

  @Test
  void testChangePasswordIncorrectCurrentPassword() {
    ChangePasswordRequest request =
        new ChangePasswordRequest("wrongPassword", "newPassword123", "newPassword123");

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

    assertThrows(AuthenticationException.class, () -> authService.changePassword(request));

    verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testChangePasswordMismatchedPasswords() {
    ChangePasswordRequest request =
        new ChangePasswordRequest("oldPassword", "newPassword123", "differentPassword");

    Authentication authentication =
        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

    assertThrows(AuthenticationException.class, () -> authService.changePassword(request));

    verify(passwordEncoder).matches("oldPassword", "encodedPassword");
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testForgotPasswordSuccess() throws MessagingException {
    ForgotPasswordRequest request = new ForgotPasswordRequest("john@example.com");
    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    lenient()
        .when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
        .thenReturn(PasswordResetToken.builder().build());

    authService.forgotPassword(request);

    verify(userRepository).findByEmail("john@example.com");
    verify(passwordResetTokenRepository).deleteByUser(user);
    verify(passwordResetTokenRepository).flush();
    verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    verify(emailService).sendPasswordResetEmail(eq("john@example.com"), anyString());
  }

  @Test
  void testForgotPasswordUserNotFound() {
    ForgotPasswordRequest request = new ForgotPasswordRequest("nonexistent@example.com");
    when(userRepository.findByEmail("nonexistent@example.com"))
        .thenReturn(java.util.Optional.empty());

    assertThrows(AuthenticationException.class, () -> authService.forgotPassword(request));

    verify(userRepository).findByEmail("nonexistent@example.com");
    verify(passwordResetTokenRepository, never()).deleteByUser(any(User.class));
    try {
      verify(emailService, never()).sendPasswordResetEmail(anyString(), anyString());
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testForgotPasswordEmailSendingFailure() throws MessagingException {
    ForgotPasswordRequest request = new ForgotPasswordRequest("john@example.com");
    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    lenient()
        .when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
        .thenReturn(PasswordResetToken.builder().build());
    doThrow(new MessagingException("Email error"))
        .when(emailService)
        .sendPasswordResetEmail(anyString(), anyString());

    assertThrows(EmailSendingException.class, () -> authService.forgotPassword(request));

    verify(userRepository).findByEmail("john@example.com");
    verify(passwordResetTokenRepository).deleteByUser(user);
    verify(emailService).sendPasswordResetEmail(eq("john@example.com"), anyString());
  }

  @Test
  void testResetPasswordSuccess() {
    ResetPasswordRequest request =
        new ResetPasswordRequest("valid-token", "newPassword123", "newPassword123");

    PasswordResetToken resetToken =
        PasswordResetToken.builder()
            .token("valid-token")
            .expiryDate(Instant.now().plusSeconds(900))
            .user(user)
            .build();

    when(passwordResetTokenRepository.findByToken("valid-token"))
        .thenReturn(java.util.Optional.of(resetToken));
    when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);

    authService.resetPassword(request);

    verify(passwordResetTokenRepository).findByToken("valid-token");
    verify(passwordEncoder).encode("newPassword123");
    verify(userRepository).save(any(User.class));
    verify(passwordResetTokenRepository).delete(resetToken);
    verify(refreshTokenService).deleteByUser(user);
  }

  @Test
  void testResetPasswordInvalidToken() {
    ResetPasswordRequest request =
        new ResetPasswordRequest("invalid-token", "newPassword123", "newPassword123");

    when(passwordResetTokenRepository.findByToken("invalid-token"))
        .thenReturn(java.util.Optional.empty());

    assertThrows(AuthenticationException.class, () -> authService.resetPassword(request));

    verify(passwordResetTokenRepository).findByToken("invalid-token");
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testResetPasswordExpiredToken() {
    ResetPasswordRequest request =
        new ResetPasswordRequest("expired-token", "newPassword123", "newPassword123");

    PasswordResetToken resetToken =
        PasswordResetToken.builder()
            .token("expired-token")
            .expiryDate(Instant.now().minusSeconds(900))
            .user(user)
            .build();

    when(passwordResetTokenRepository.findByToken("expired-token"))
        .thenReturn(java.util.Optional.of(resetToken));

    assertThrows(AuthenticationException.class, () -> authService.resetPassword(request));

    verify(passwordResetTokenRepository).findByToken("expired-token");
    verify(passwordResetTokenRepository).delete(resetToken);
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testResetPasswordMismatchedPasswords() {
    ResetPasswordRequest request =
        new ResetPasswordRequest("valid-token", "newPassword123", "differentPassword");

    PasswordResetToken resetToken =
        PasswordResetToken.builder()
            .token("valid-token")
            .expiryDate(Instant.now().plusSeconds(900))
            .user(user)
            .build();

    when(passwordResetTokenRepository.findByToken("valid-token"))
        .thenReturn(java.util.Optional.of(resetToken));

    assertThrows(AuthenticationException.class, () -> authService.resetPassword(request));

    verify(passwordResetTokenRepository).findByToken("valid-token");
    verify(passwordEncoder, never()).encode(anyString());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testSendOtpSuccess() throws MessagingException {
    SendOtpRequest request = new SendOtpRequest("john@example.com");
    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));

    authService.sendOtp(request);

    verify(userRepository).findByEmail("john@example.com");
    verify(otpTokenRepository).deleteByUser(user);
    verify(otpTokenRepository).save(any(OtpToken.class));
    verify(emailService).sendOtpEmail(eq("john@example.com"), anyString());
  }

  @Test
  void testSendOtpUserNotFound() {
    SendOtpRequest request = new SendOtpRequest("nonexistent@example.com");
    when(userRepository.findByEmail("nonexistent@example.com"))
        .thenReturn(java.util.Optional.empty());

    assertThrows(AuthenticationException.class, () -> authService.sendOtp(request));

    verify(userRepository).findByEmail("nonexistent@example.com");
    verify(otpTokenRepository, never()).deleteByUser(any(User.class));
    verify(otpTokenRepository, never()).save(any(OtpToken.class));
    try {
      verify(emailService, never()).sendOtpEmail(anyString(), anyString());
    } catch (MessagingException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void testSendOtpEmailSendingFailure() throws MessagingException {
    SendOtpRequest request = new SendOtpRequest("john@example.com");
    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    doThrow(new MessagingException("Email error"))
        .when(emailService)
        .sendOtpEmail(anyString(), anyString());

    assertThrows(OtpEmailSendingException.class, () -> authService.sendOtp(request));

    verify(userRepository).findByEmail("john@example.com");
    verify(otpTokenRepository).deleteByUser(user);
    verify(emailService).sendOtpEmail(eq("john@example.com"), anyString());
  }

  @Test
  void testVerifyOtpSuccess() {
    VerifyOtpRequest request = new VerifyOtpRequest("john@example.com", "123456");

    OtpToken otpToken =
        OtpToken.builder()
            .otp("123456")
            .expiryDate(Instant.now().plusSeconds(300))
            .verified(false)
            .user(user)
            .build();

    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    when(otpTokenRepository.findByUser(user)).thenReturn(java.util.Optional.of(otpToken));
    when(otpTokenRepository.save(any(OtpToken.class))).thenReturn(otpToken);

    authService.verifyOtp(request);

    verify(userRepository).findByEmail("john@example.com");
    verify(otpTokenRepository).findByUser(user);
    verify(otpTokenRepository).save(any(OtpToken.class));
    assertTrue(otpToken.isVerified());
  }

  @Test
  void testVerifyOtpUserNotFound() {
    VerifyOtpRequest request = new VerifyOtpRequest("nonexistent@example.com", "123456");
    when(userRepository.findByEmail("nonexistent@example.com"))
        .thenReturn(java.util.Optional.empty());

    assertThrows(AuthenticationException.class, () -> authService.verifyOtp(request));

    verify(userRepository).findByEmail("nonexistent@example.com");
    verify(otpTokenRepository, never()).findByUser(any(User.class));
  }

  @Test
  void testVerifyOtpOtpNotFound() {
    VerifyOtpRequest request = new VerifyOtpRequest("john@example.com", "123456");
    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    when(otpTokenRepository.findByUser(user)).thenReturn(java.util.Optional.empty());

    assertThrows(AuthenticationException.class, () -> authService.verifyOtp(request));

    verify(userRepository).findByEmail("john@example.com");
    verify(otpTokenRepository).findByUser(user);
  }

  @Test
  void testVerifyOtpExpiredOtp() {
    VerifyOtpRequest request = new VerifyOtpRequest("john@example.com", "123456");

    OtpToken otpToken =
        OtpToken.builder()
            .otp("123456")
            .expiryDate(Instant.now().minusSeconds(300))
            .verified(false)
            .user(user)
            .build();

    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    when(otpTokenRepository.findByUser(user)).thenReturn(java.util.Optional.of(otpToken));

    assertThrows(AuthenticationException.class, () -> authService.verifyOtp(request));

    verify(userRepository).findByEmail("john@example.com");
    verify(otpTokenRepository).findByUser(user);
    verify(otpTokenRepository).delete(otpToken);
  }

  @Test
  void testVerifyOtpInvalidOtp() {
    VerifyOtpRequest request = new VerifyOtpRequest("john@example.com", "wrong-otp");

    OtpToken otpToken =
        OtpToken.builder()
            .otp("123456")
            .expiryDate(Instant.now().plusSeconds(300))
            .verified(false)
            .user(user)
            .build();

    when(userRepository.findByEmail("john@example.com")).thenReturn(java.util.Optional.of(user));
    when(otpTokenRepository.findByUser(user)).thenReturn(java.util.Optional.of(otpToken));

    assertThrows(AuthenticationException.class, () -> authService.verifyOtp(request));

    verify(userRepository).findByEmail("john@example.com");
    verify(otpTokenRepository).findByUser(user);
    verify(otpTokenRepository, never()).save(any(OtpToken.class));
  }
}

package com.digitalpayment.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.digitalpayment.auth.config.JwtProperties;
import com.digitalpayment.auth.entity.RefreshToken;
import com.digitalpayment.auth.entity.User;
import com.digitalpayment.auth.exception.AuthenticationException;
import com.digitalpayment.auth.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

  @Mock private RefreshTokenRepository refreshTokenRepository;

  @Mock private JwtProperties jwtProperties;

  @InjectMocks private RefreshTokenServiceImpl refreshTokenService;

  private User user;
  private RefreshToken refreshToken;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("test@example.com");

    refreshToken = new RefreshToken();
    refreshToken.setId(1L);
    refreshToken.setToken("test-token");
    refreshToken.setExpiryDate(Instant.now().plusMillis(3600000));
    refreshToken.setUser(user);
  }

  @Test
  void testCreateRefreshToken_Success() {
    when(jwtProperties.getRefreshExpiration()).thenReturn(86400000L);
    when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());
    when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

    RefreshToken result = refreshTokenService.createRefreshToken(user);

    assertNotNull(result);
    verify(refreshTokenRepository).findByUser(user);
    verify(refreshTokenRepository).save(any(RefreshToken.class));
  }

  @Test
  void testCreateRefreshToken_VerifyTokenGeneration() {
    when(jwtProperties.getRefreshExpiration()).thenReturn(86400000L);
    when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());
    when(refreshTokenRepository.save(any(RefreshToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RefreshToken result = refreshTokenService.createRefreshToken(user);

    assertNotNull(result);
    assertNotNull(result.getToken());
    assertEquals(user, result.getUser());
    assertTrue(result.getExpiryDate().isAfter(Instant.now()));
  }

  @Test
  void testFindByToken_Found() {
    String token = "test-token";
    when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

    Optional<RefreshToken> result = refreshTokenService.findByToken(token);

    assertTrue(result.isPresent());
    assertEquals(refreshToken, result.get());
    verify(refreshTokenRepository).findByToken(token);
  }

  @Test
  void testFindByToken_NotFound() {
    String token = "non-existent-token";
    when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

    Optional<RefreshToken> result = refreshTokenService.findByToken(token);

    assertFalse(result.isPresent());
    verify(refreshTokenRepository).findByToken(token);
  }

  @Test
  void testVerifyExpiration_ValidToken() {
    Instant futureExpiry = Instant.now().plusMillis(3600000);
    refreshToken.setExpiryDate(futureExpiry);

    RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

    assertNotNull(result);
    assertEquals(refreshToken, result);
    verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
  }

  @Test
  void testVerifyExpiration_ExpiredToken() {
    Instant pastExpiry = Instant.now().minusMillis(3600000);
    refreshToken.setExpiryDate(pastExpiry);

    assertThrows(
        AuthenticationException.class, () -> refreshTokenService.verifyExpiration(refreshToken));

    verify(refreshTokenRepository).delete(refreshToken);
  }

  @Test
  void testVerifyExpiration_JustExpiredToken() {
    Instant justExpired = Instant.now().minusMillis(1000);
    refreshToken.setExpiryDate(justExpired);

    assertThrows(
        AuthenticationException.class, () -> refreshTokenService.verifyExpiration(refreshToken));

    verify(refreshTokenRepository).delete(refreshToken);
  }

  @Test
  void testDeleteByUser() {
    refreshTokenService.deleteByUser(user);

    verify(refreshTokenRepository).deleteByUser(user);
  }

  @Test
  void testCreateRefreshToken_UpdatesExistingTokens() {
    when(jwtProperties.getRefreshExpiration()).thenReturn(86400000L);
    when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.of(refreshToken));
    when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

    RefreshToken result = refreshTokenService.createRefreshToken(user);

    assertNotNull(result);
    verify(refreshTokenRepository).findByUser(user);
    verify(refreshTokenRepository).save(any(RefreshToken.class));
  }

  @Test
  void testCreateRefreshToken_SetsCorrectExpiry() {
    long expectedExpiration = 86400000L;
    when(jwtProperties.getRefreshExpiration()).thenReturn(expectedExpiration);
    when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());
    when(refreshTokenRepository.save(any(RefreshToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RefreshToken result = refreshTokenService.createRefreshToken(user);

    assertNotNull(result);
    Instant expectedExpiry = Instant.now().plusMillis(expectedExpiration);
    assertTrue(result.getExpiryDate().isAfter(Instant.now()));
    assertTrue(result.getExpiryDate().isBefore(expectedExpiry.plusMillis(1000)));
  }

  @Test
  void testCreateRefreshToken_GeneratesUniqueToken() {
    when(jwtProperties.getRefreshExpiration()).thenReturn(86400000L);
    when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.empty());
    when(refreshTokenRepository.save(any(RefreshToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    RefreshToken result1 = refreshTokenService.createRefreshToken(user);
    RefreshToken result2 = refreshTokenService.createRefreshToken(user);

    assertNotEquals(result1.getToken(), result2.getToken());
  }

  @Test
  void testRevokeToken_Success() {
    String token = "test-token";
    when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(refreshToken));

    refreshTokenService.revokeToken(token);

    verify(refreshTokenRepository).findByToken(token);
    verify(refreshTokenRepository).delete(refreshToken);
  }

  @Test
  void testRevokeToken_InvalidToken() {
    String token = "invalid-token";
    when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

    assertThrows(AuthenticationException.class, () -> refreshTokenService.revokeToken(token));

    verify(refreshTokenRepository).findByToken(token);
    verify(refreshTokenRepository, never()).delete(any(RefreshToken.class));
  }
}

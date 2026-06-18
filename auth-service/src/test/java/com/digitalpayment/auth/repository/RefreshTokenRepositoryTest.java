package com.digitalpayment.auth.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.digitalpayment.auth.entity.RefreshToken;
import com.digitalpayment.auth.entity.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryTest {

  @Mock private RefreshTokenRepository refreshTokenRepository;

  private User user;
  private RefreshToken refreshToken;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("john@example.com");
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setPassword("encodedPassword");

    refreshToken = new RefreshToken();
    refreshToken.setId(1L);
    refreshToken.setToken("refresh-token-123");
    refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
    refreshToken.setUser(user);
  }

  @Test
  void testFindByToken() {
    when(refreshTokenRepository.findByToken("refresh-token-123"))
        .thenReturn(Optional.of(refreshToken));

    Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("refresh-token-123");

    assertTrue(foundToken.isPresent());
    assertEquals("refresh-token-123", foundToken.get().getToken());
    assertEquals(user.getId(), foundToken.get().getUser().getId());
    verify(refreshTokenRepository).findByToken("refresh-token-123");
  }

  @Test
  void testFindByTokenNotFound() {
    when(refreshTokenRepository.findByToken("non-existent-token")).thenReturn(Optional.empty());

    Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken("non-existent-token");

    assertFalse(foundToken.isPresent());
    verify(refreshTokenRepository).findByToken("non-existent-token");
  }

  @Test
  void testFindByTokenWithNull() {
    when(refreshTokenRepository.findByToken(null)).thenReturn(Optional.empty());

    Optional<RefreshToken> foundToken = refreshTokenRepository.findByToken(null);

    assertFalse(foundToken.isPresent());
    verify(refreshTokenRepository).findByToken(null);
  }

  @Test
  void testFindByUser() {
    when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.of(refreshToken));

    Optional<RefreshToken> foundToken = refreshTokenRepository.findByUser(user);

    assertTrue(foundToken.isPresent());
    assertEquals("refresh-token-123", foundToken.get().getToken());
    assertEquals(user.getId(), foundToken.get().getUser().getId());
    verify(refreshTokenRepository).findByUser(user);
  }

  @Test
  void testFindByUserNotFound() {
    User newUser = new User();
    newUser.setId(UUID.randomUUID());
    newUser.setEmail("jane@example.com");

    when(refreshTokenRepository.findByUser(newUser)).thenReturn(Optional.empty());

    Optional<RefreshToken> foundToken = refreshTokenRepository.findByUser(newUser);

    assertFalse(foundToken.isPresent());
    verify(refreshTokenRepository).findByUser(newUser);
  }

  @Test
  void testDeleteByUser() {
    doNothing().when(refreshTokenRepository).deleteByUser(user);

    refreshTokenRepository.deleteByUser(user);

    verify(refreshTokenRepository).deleteByUser(user);
  }

  @Test
  void testSaveRefreshToken() {
    RefreshToken newToken = new RefreshToken();
    newToken.setId(2L);
    newToken.setToken("new-refresh-token");
    newToken.setExpiryDate(Instant.now().plusSeconds(7200));
    newToken.setUser(user);

    when(refreshTokenRepository.save(newToken)).thenReturn(newToken);

    RefreshToken savedToken = refreshTokenRepository.save(newToken);

    assertNotNull(savedToken);
    assertEquals(2L, savedToken.getId());
    assertEquals("new-refresh-token", savedToken.getToken());
    verify(refreshTokenRepository).save(newToken);
  }

  @Test
  void testDeleteRefreshToken() {
    doNothing().when(refreshTokenRepository).delete(refreshToken);

    refreshTokenRepository.delete(refreshToken);

    verify(refreshTokenRepository).delete(refreshToken);
  }

  @Test
  void testCount() {
    when(refreshTokenRepository.count()).thenReturn(1L);

    long count = refreshTokenRepository.count();

    assertEquals(1, count);
    verify(refreshTokenRepository).count();
  }
}

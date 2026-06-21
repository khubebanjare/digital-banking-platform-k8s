package com.digitalpayment.auth.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.digitalpayment.auth.entity.PasswordResetToken;
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
class PasswordResetTokenRepositoryTest {

  @Mock private PasswordResetTokenRepository passwordResetTokenRepository;

  private User user;
  private PasswordResetToken passwordResetToken;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("john@example.com");
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setPassword("encodedPassword");

    passwordResetToken = new PasswordResetToken();
    passwordResetToken.setId(UUID.randomUUID());
    passwordResetToken.setToken("reset-token-123");
    passwordResetToken.setExpiryDate(Instant.now().plusSeconds(3600));
    passwordResetToken.setUser(user);
  }

  @Test
  void testFindByToken() {
    when(passwordResetTokenRepository.findByToken("reset-token-123"))
        .thenReturn(Optional.of(passwordResetToken));

    Optional<PasswordResetToken> foundToken =
        passwordResetTokenRepository.findByToken("reset-token-123");

    assertTrue(foundToken.isPresent());
    assertEquals("reset-token-123", foundToken.get().getToken());
    assertEquals(user.getId(), foundToken.get().getUser().getId());
    verify(passwordResetTokenRepository).findByToken("reset-token-123");
  }

  @Test
  void testFindByTokenNotFound() {
    when(passwordResetTokenRepository.findByToken("non-existent-token"))
        .thenReturn(Optional.empty());

    Optional<PasswordResetToken> foundToken =
        passwordResetTokenRepository.findByToken("non-existent-token");

    assertFalse(foundToken.isPresent());
    verify(passwordResetTokenRepository).findByToken("non-existent-token");
  }

  @Test
  void testFindByTokenWithNull() {
    when(passwordResetTokenRepository.findByToken(null)).thenReturn(Optional.empty());

    Optional<PasswordResetToken> foundToken = passwordResetTokenRepository.findByToken(null);

    assertFalse(foundToken.isPresent());
    verify(passwordResetTokenRepository).findByToken(null);
  }

  @Test
  void testSavePasswordResetToken() {
    PasswordResetToken newToken = new PasswordResetToken();
    newToken.setId(UUID.randomUUID());
    newToken.setToken("new-reset-token");
    newToken.setExpiryDate(Instant.now().plusSeconds(7200));
    newToken.setUser(user);

    when(passwordResetTokenRepository.save(newToken)).thenReturn(newToken);

    PasswordResetToken savedToken = passwordResetTokenRepository.save(newToken);

    assertNotNull(savedToken);
    assertNotNull(savedToken.getId());
    assertEquals("new-reset-token", savedToken.getToken());
    verify(passwordResetTokenRepository).save(newToken);
  }

  @Test
  void testDeletePasswordResetToken() {
    doNothing().when(passwordResetTokenRepository).delete(passwordResetToken);

    passwordResetTokenRepository.delete(passwordResetToken);

    verify(passwordResetTokenRepository).delete(passwordResetToken);
  }

  @Test
  void testFindById() {
    UUID tokenId = passwordResetToken.getId();
    when(passwordResetTokenRepository.findById(tokenId))
        .thenReturn(Optional.of(passwordResetToken));

    Optional<PasswordResetToken> foundToken = passwordResetTokenRepository.findById(tokenId);

    assertTrue(foundToken.isPresent());
    assertEquals(tokenId, foundToken.get().getId());
    verify(passwordResetTokenRepository).findById(tokenId);
  }

  @Test
  void testCount() {
    when(passwordResetTokenRepository.count()).thenReturn(1L);

    long count = passwordResetTokenRepository.count();

    assertEquals(1, count);
    verify(passwordResetTokenRepository).count();
  }
}

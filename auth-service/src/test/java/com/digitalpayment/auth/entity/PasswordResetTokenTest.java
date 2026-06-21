package com.digitalpayment.auth.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PasswordResetTokenTest {

  private PasswordResetToken passwordResetToken;
  private UUID tokenId;
  private User user;

  @BeforeEach
  void setUp() {
    tokenId = UUID.randomUUID();
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("john@example.com");

    passwordResetToken = new PasswordResetToken();
    passwordResetToken.setId(tokenId);
    passwordResetToken.setToken("reset-token-123");
    passwordResetToken.setExpiryDate(Instant.now().plusSeconds(3600));
    passwordResetToken.setUser(user);
  }

  @Test
  void testPasswordResetTokenCreation() {
    assertNotNull(passwordResetToken);
    assertEquals(tokenId, passwordResetToken.getId());
    assertEquals("reset-token-123", passwordResetToken.getToken());
    assertNotNull(passwordResetToken.getExpiryDate());
    assertNotNull(passwordResetToken.getUser());
  }

  @Test
  void testPasswordResetTokenSetters() {
    UUID newId = UUID.randomUUID();
    User newUser = new User();
    newUser.setId(UUID.randomUUID());
    newUser.setEmail("jane@example.com");

    passwordResetToken.setId(newId);
    passwordResetToken.setToken("new-reset-token");
    passwordResetToken.setExpiryDate(Instant.now().plusSeconds(7200));
    passwordResetToken.setUser(newUser);

    assertEquals(newId, passwordResetToken.getId());
    assertEquals("new-reset-token", passwordResetToken.getToken());
    assertEquals(newUser, passwordResetToken.getUser());
  }

  @Test
  void testPasswordResetTokenNoArgsConstructor() {
    PasswordResetToken newToken = new PasswordResetToken();
    assertNotNull(newToken);
  }

  @Test
  void testPasswordResetTokenAllArgsConstructor() {
    UUID id = UUID.randomUUID();
    String token = "test-token";
    Instant expiryDate = Instant.now().plusSeconds(3600);
    User testUser = new User();
    testUser.setId(UUID.randomUUID());

    PasswordResetToken newToken = new PasswordResetToken(id, token, expiryDate, testUser);

    assertEquals(id, newToken.getId());
    assertEquals(token, newToken.getToken());
    assertEquals(expiryDate, newToken.getExpiryDate());
    assertEquals(testUser, newToken.getUser());
  }

  @Test
  void testPasswordResetTokenBuilder() {
    UUID id = UUID.randomUUID();
    String token = "builder-token";
    Instant expiryDate = Instant.now().plusSeconds(3600);
    User testUser = new User();
    testUser.setId(UUID.randomUUID());

    PasswordResetToken builtToken =
        PasswordResetToken.builder()
            .id(id)
            .token(token)
            .expiryDate(expiryDate)
            .user(testUser)
            .build();

    assertEquals(id, builtToken.getId());
    assertEquals(token, builtToken.getToken());
    assertEquals(expiryDate, builtToken.getExpiryDate());
    assertEquals(testUser, builtToken.getUser());
  }

  @Test
  void testPasswordResetTokenWithNullValues() {
    PasswordResetToken nullToken = new PasswordResetToken();
    nullToken.setId(null);
    nullToken.setToken(null);
    nullToken.setExpiryDate(null);
    nullToken.setUser(null);

    assertNull(nullToken.getId());
    assertNull(nullToken.getToken());
    assertNull(nullToken.getExpiryDate());
    assertNull(nullToken.getUser());
  }

  @Test
  void testPasswordResetTokenEquality() {
    PasswordResetToken token1 = passwordResetToken;
    PasswordResetToken token2 = passwordResetToken;

    assertEquals(token1, token2);
  }

  @Test
  void testPasswordResetTokenToString() {
    String toString = passwordResetToken.toString();
    assertNotNull(toString);
  }
}

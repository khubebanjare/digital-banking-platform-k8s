package com.digitalpayment.auth.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RefreshTokenTest {

  private RefreshToken refreshToken;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("john@example.com");

    refreshToken = new RefreshToken();
    refreshToken.setId(1L);
    refreshToken.setToken("refresh-token-123");
    refreshToken.setExpiryDate(Instant.now().plusSeconds(3600));
    refreshToken.setUser(user);
  }

  @Test
  void testRefreshTokenCreation() {
    assertNotNull(refreshToken);
    assertEquals(1L, refreshToken.getId());
    assertEquals("refresh-token-123", refreshToken.getToken());
    assertNotNull(refreshToken.getExpiryDate());
    assertNotNull(refreshToken.getUser());
  }

  @Test
  void testRefreshTokenSetters() {
    User newUser = new User();
    newUser.setId(UUID.randomUUID());
    newUser.setEmail("jane@example.com");

    refreshToken.setId(2L);
    refreshToken.setToken("new-refresh-token");
    refreshToken.setExpiryDate(Instant.now().plusSeconds(7200));
    refreshToken.setUser(newUser);

    assertEquals(2L, refreshToken.getId());
    assertEquals("new-refresh-token", refreshToken.getToken());
    assertEquals(newUser, refreshToken.getUser());
  }

  @Test
  void testRefreshTokenNoArgsConstructor() {
    RefreshToken newToken = new RefreshToken();
    assertNotNull(newToken);
  }

  @Test
  void testRefreshTokenAllArgsConstructor() {
    Long id = 1L;
    String token = "test-token";
    Instant expiryDate = Instant.now().plusSeconds(3600);
    User testUser = new User();
    testUser.setId(UUID.randomUUID());

    RefreshToken newToken = new RefreshToken(id, token, expiryDate, testUser);

    assertEquals(id, newToken.getId());
    assertEquals(token, newToken.getToken());
    assertEquals(expiryDate, newToken.getExpiryDate());
    assertEquals(testUser, newToken.getUser());
  }

  @Test
  void testRefreshTokenBuilder() {
    Long id = 1L;
    String token = "builder-token";
    Instant expiryDate = Instant.now().plusSeconds(3600);
    User testUser = new User();
    testUser.setId(UUID.randomUUID());

    RefreshToken builtToken =
        RefreshToken.builder().id(id).token(token).expiryDate(expiryDate).user(testUser).build();

    assertEquals(id, builtToken.getId());
    assertEquals(token, builtToken.getToken());
    assertEquals(expiryDate, builtToken.getExpiryDate());
    assertEquals(testUser, builtToken.getUser());
  }

  @Test
  void testRefreshTokenWithNullValues() {
    RefreshToken nullToken = new RefreshToken();
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
  void testRefreshTokenEquality() {
    RefreshToken token1 = refreshToken;
    RefreshToken token2 = refreshToken;

    assertEquals(token1, token2);
  }

  @Test
  void testRefreshTokenToString() {
    String toString = refreshToken.toString();
    assertNotNull(toString);
  }
}

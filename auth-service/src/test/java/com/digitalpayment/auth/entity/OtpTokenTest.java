package com.digitalpayment.auth.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OtpTokenTest {

  private OtpToken otpToken;
  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail("test@example.com");

    otpToken = new OtpToken();
    otpToken.setId(UUID.randomUUID());
    otpToken.setOtp("123456");
    otpToken.setExpiryDate(Instant.now().plusSeconds(300));
    otpToken.setVerified(false);
    otpToken.setUser(user);
  }

  @Test
  void testOtpTokenConstructor() {
    OtpToken token = new OtpToken();

    assertNotNull(token);
  }

  @Test
  void testOtpTokenBuilder() {
    OtpToken token =
        OtpToken.builder()
            .otp("123456")
            .expiryDate(Instant.now().plusSeconds(300))
            .verified(false)
            .user(user)
            .build();

    assertEquals("123456", token.getOtp());
    assertFalse(token.isVerified());
    assertEquals(user, token.getUser());
  }

  @Test
  void testOtpTokenGettersAndSetters() {
    otpToken.setOtp("654321");
    otpToken.setVerified(true);
    otpToken.setExpiryDate(Instant.now().plusSeconds(600));

    assertEquals("654321", otpToken.getOtp());
    assertTrue(otpToken.isVerified());
    assertNotNull(otpToken.getExpiryDate());
  }

  @Test
  void testOtpTokenAllArgsConstructor() {
    UUID id = UUID.randomUUID();
    OtpToken token = new OtpToken(id, "123456", Instant.now().plusSeconds(300), false, user);

    assertEquals(id, token.getId());
    assertEquals("123456", token.getOtp());
    assertFalse(token.isVerified());
    assertEquals(user, token.getUser());
  }

  @Test
  void testOtpTokenEquals() {
    UUID id = UUID.randomUUID();
    OtpToken token1 = new OtpToken();
    token1.setId(id);
    OtpToken token2 = new OtpToken();
    token2.setId(id);

    assertEquals(token1, token2);
  }

  @Test
  void testOtpTokenNotEquals() {
    OtpToken token1 = new OtpToken();
    token1.setId(UUID.randomUUID());
    OtpToken token2 = new OtpToken();
    token2.setId(UUID.randomUUID());

    assertNotEquals(token1, token2);
  }

  @Test
  void testOtpTokenHashCode() {
    UUID id = UUID.randomUUID();
    OtpToken token1 = new OtpToken();
    token1.setId(id);
    OtpToken token2 = new OtpToken();
    token2.setId(id);

    assertEquals(token1.hashCode(), token2.hashCode());
  }

  @Test
  void testOtpTokenToString() {
    String toString = otpToken.toString();

    assertTrue(toString.contains("123456"));
  }

  @Test
  void testOtpTokenWithNullUser() {
    OtpToken token = new OtpToken();
    token.setUser(null);

    assertNull(token.getUser());
  }

  @Test
  void testOtpTokenWithNullOtp() {
    OtpToken token = new OtpToken();
    token.setOtp(null);

    assertNull(token.getOtp());
  }

  @Test
  void testOtpTokenWithPastExpiryDate() {
    OtpToken token = new OtpToken();
    token.setExpiryDate(Instant.now().minusSeconds(300));

    assertTrue(token.getExpiryDate().isBefore(Instant.now()));
  }

  @Test
  void testOtpTokenWithFutureExpiryDate() {
    OtpToken token = new OtpToken();
    token.setExpiryDate(Instant.now().plusSeconds(300));

    assertTrue(token.getExpiryDate().isAfter(Instant.now()));
  }
}

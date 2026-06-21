package com.digitalpayment.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class RefreshTokenRequestTest {

  @Test
  void testRefreshTokenRequestCreation() {
    RefreshTokenRequest request = new RefreshTokenRequest("refresh-token-123");

    assertEquals("refresh-token-123", request.refreshToken());
  }

  @Test
  void testRefreshTokenRequestWithNullToken() {
    RefreshTokenRequest request = new RefreshTokenRequest(null);

    assertEquals(null, request.refreshToken());
  }

  @Test
  void testRefreshTokenRequestWithEmptyToken() {
    RefreshTokenRequest request = new RefreshTokenRequest("");

    assertEquals("", request.refreshToken());
  }

  @Test
  void testRefreshTokenRequestEquals() {
    RefreshTokenRequest request1 = new RefreshTokenRequest("refresh-token-123");
    RefreshTokenRequest request2 = new RefreshTokenRequest("refresh-token-123");

    assertEquals(request1, request2);
  }

  @Test
  void testRefreshTokenRequestHashCode() {
    RefreshTokenRequest request1 = new RefreshTokenRequest("refresh-token-123");
    RefreshTokenRequest request2 = new RefreshTokenRequest("refresh-token-123");

    assertEquals(request1.hashCode(), request2.hashCode());
  }

  @Test
  void testRefreshTokenRequestToString() {
    RefreshTokenRequest request = new RefreshTokenRequest("refresh-token-123");

    assertNotNull(request.toString());
  }
}

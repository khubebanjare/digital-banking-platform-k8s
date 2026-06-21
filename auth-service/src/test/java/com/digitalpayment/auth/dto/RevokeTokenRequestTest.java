package com.digitalpayment.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class RevokeTokenRequestTest {

  @Test
  void testRevokeTokenRequestCreation() {
    RevokeTokenRequest request = new RevokeTokenRequest("refresh-token-123");

    assertEquals("refresh-token-123", request.refreshToken());
  }

  @Test
  void testRevokeTokenRequestWithNullToken() {
    RevokeTokenRequest request = new RevokeTokenRequest(null);

    assertEquals(null, request.refreshToken());
  }

  @Test
  void testRevokeTokenRequestWithEmptyToken() {
    RevokeTokenRequest request = new RevokeTokenRequest("");

    assertEquals("", request.refreshToken());
  }

  @Test
  void testRevokeTokenRequestEquals() {
    RevokeTokenRequest request1 = new RevokeTokenRequest("refresh-token-123");
    RevokeTokenRequest request2 = new RevokeTokenRequest("refresh-token-123");

    assertEquals(request1, request2);
  }

  @Test
  void testRevokeTokenRequestHashCode() {
    RevokeTokenRequest request1 = new RevokeTokenRequest("refresh-token-123");
    RevokeTokenRequest request2 = new RevokeTokenRequest("refresh-token-123");

    assertEquals(request1.hashCode(), request2.hashCode());
  }

  @Test
  void testRevokeTokenRequestToString() {
    RevokeTokenRequest request = new RevokeTokenRequest("refresh-token-123");

    assertNotNull(request.toString());
  }
}

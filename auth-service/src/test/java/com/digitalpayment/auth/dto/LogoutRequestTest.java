package com.digitalpayment.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class LogoutRequestTest {

  @Test
  void testLogoutRequestCreation() {
    LogoutRequest request = new LogoutRequest("refresh-token-123");

    assertEquals("refresh-token-123", request.refreshToken());
  }

  @Test
  void testLogoutRequestWithNullToken() {
    LogoutRequest request = new LogoutRequest(null);

    assertEquals(null, request.refreshToken());
  }

  @Test
  void testLogoutRequestWithEmptyToken() {
    LogoutRequest request = new LogoutRequest("");

    assertEquals("", request.refreshToken());
  }

  @Test
  void testLogoutRequestEquals() {
    LogoutRequest request1 = new LogoutRequest("refresh-token-123");
    LogoutRequest request2 = new LogoutRequest("refresh-token-123");

    assertEquals(request1, request2);
  }

  @Test
  void testLogoutRequestHashCode() {
    LogoutRequest request1 = new LogoutRequest("refresh-token-123");
    LogoutRequest request2 = new LogoutRequest("refresh-token-123");

    assertEquals(request1.hashCode(), request2.hashCode());
  }

  @Test
  void testLogoutRequestToString() {
    LogoutRequest request = new LogoutRequest("refresh-token-123");

    assertNotNull(request.toString());
  }
}

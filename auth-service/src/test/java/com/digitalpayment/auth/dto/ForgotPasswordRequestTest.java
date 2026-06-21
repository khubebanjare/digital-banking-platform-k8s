package com.digitalpayment.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ForgotPasswordRequestTest {

  @Test
  void testForgotPasswordRequestCreation() {
    ForgotPasswordRequest request = new ForgotPasswordRequest("john@example.com");

    assertEquals("john@example.com", request.email());
  }

  @Test
  void testForgotPasswordRequestWithNullEmail() {
    ForgotPasswordRequest request = new ForgotPasswordRequest(null);

    assertEquals(null, request.email());
  }

  @Test
  void testForgotPasswordRequestWithEmptyEmail() {
    ForgotPasswordRequest request = new ForgotPasswordRequest("");

    assertEquals("", request.email());
  }

  @Test
  void testForgotPasswordRequestEquals() {
    ForgotPasswordRequest request1 = new ForgotPasswordRequest("john@example.com");
    ForgotPasswordRequest request2 = new ForgotPasswordRequest("john@example.com");

    assertEquals(request1, request2);
  }

  @Test
  void testForgotPasswordRequestHashCode() {
    ForgotPasswordRequest request1 = new ForgotPasswordRequest("john@example.com");
    ForgotPasswordRequest request2 = new ForgotPasswordRequest("john@example.com");

    assertEquals(request1.hashCode(), request2.hashCode());
  }

  @Test
  void testForgotPasswordRequestToString() {
    ForgotPasswordRequest request = new ForgotPasswordRequest("john@example.com");

    assertNotNull(request.toString());
  }
}

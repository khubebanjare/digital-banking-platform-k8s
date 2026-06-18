package com.digitalpayment.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ResetPasswordRequestTest {

  @Test
  void testResetPasswordRequestCreation() {
    ResetPasswordRequest request =
        new ResetPasswordRequest("reset-token-123", "newPassword123", "newPassword123");

    assertEquals("reset-token-123", request.token());
    assertEquals("newPassword123", request.newPassword());
    assertEquals("newPassword123", request.confirmPassword());
  }

  @Test
  void testResetPasswordRequestWithDifferentConfirmPassword() {
    ResetPasswordRequest request =
        new ResetPasswordRequest("reset-token-123", "newPassword123", "differentPassword");

    assertEquals("reset-token-123", request.token());
    assertEquals("newPassword123", request.newPassword());
    assertEquals("differentPassword", request.confirmPassword());
  }

  @Test
  void testResetPasswordRequestWithNullValues() {
    ResetPasswordRequest request = new ResetPasswordRequest(null, null, null);

    assertEquals(null, request.token());
    assertEquals(null, request.newPassword());
    assertEquals(null, request.confirmPassword());
  }

  @Test
  void testResetPasswordRequestWithEmptyStrings() {
    ResetPasswordRequest request = new ResetPasswordRequest("", "", "");

    assertEquals("", request.token());
    assertEquals("", request.newPassword());
    assertEquals("", request.confirmPassword());
  }

  @Test
  void testResetPasswordRequestEquals() {
    ResetPasswordRequest request1 =
        new ResetPasswordRequest("reset-token-123", "newPassword123", "newPassword123");
    ResetPasswordRequest request2 =
        new ResetPasswordRequest("reset-token-123", "newPassword123", "newPassword123");

    assertEquals(request1, request2);
  }

  @Test
  void testResetPasswordRequestHashCode() {
    ResetPasswordRequest request1 =
        new ResetPasswordRequest("reset-token-123", "newPassword123", "newPassword123");
    ResetPasswordRequest request2 =
        new ResetPasswordRequest("reset-token-123", "newPassword123", "newPassword123");

    assertEquals(request1.hashCode(), request2.hashCode());
  }

  @Test
  void testResetPasswordRequestToString() {
    ResetPasswordRequest request =
        new ResetPasswordRequest("reset-token-123", "newPassword123", "newPassword123");

    assertNotNull(request.toString());
  }
}

package com.digitalpayment.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class ChangePasswordRequestTest {

  @Test
  void testChangePasswordRequestCreation() {
    ChangePasswordRequest request =
        new ChangePasswordRequest("oldPassword123", "newPassword123", "newPassword123");

    assertEquals("oldPassword123", request.currentPassword());
    assertEquals("newPassword123", request.newPassword());
    assertEquals("newPassword123", request.confirmPassword());
  }

  @Test
  void testChangePasswordRequestWithDifferentConfirmPassword() {
    ChangePasswordRequest request =
        new ChangePasswordRequest("oldPassword123", "newPassword123", "differentPassword");

    assertEquals("oldPassword123", request.currentPassword());
    assertEquals("newPassword123", request.newPassword());
    assertEquals("differentPassword", request.confirmPassword());
  }

  @Test
  void testChangePasswordRequestWithNullValues() {
    ChangePasswordRequest request = new ChangePasswordRequest(null, null, null);

    assertEquals(null, request.currentPassword());
    assertEquals(null, request.newPassword());
    assertEquals(null, request.confirmPassword());
  }

  @Test
  void testChangePasswordRequestWithEmptyStrings() {
    ChangePasswordRequest request = new ChangePasswordRequest("", "", "");

    assertEquals("", request.currentPassword());
    assertEquals("", request.newPassword());
    assertEquals("", request.confirmPassword());
  }

  @Test
  void testChangePasswordRequestEquals() {
    ChangePasswordRequest request1 =
        new ChangePasswordRequest("oldPassword123", "newPassword123", "newPassword123");
    ChangePasswordRequest request2 =
        new ChangePasswordRequest("oldPassword123", "newPassword123", "newPassword123");

    assertEquals(request1, request2);
  }

  @Test
  void testChangePasswordRequestHashCode() {
    ChangePasswordRequest request1 =
        new ChangePasswordRequest("oldPassword123", "newPassword123", "newPassword123");
    ChangePasswordRequest request2 =
        new ChangePasswordRequest("oldPassword123", "newPassword123", "newPassword123");

    assertEquals(request1.hashCode(), request2.hashCode());
  }

  @Test
  void testChangePasswordRequestToString() {
    ChangePasswordRequest request =
        new ChangePasswordRequest("oldPassword123", "newPassword123", "newPassword123");

    assertNotNull(request.toString());
  }
}

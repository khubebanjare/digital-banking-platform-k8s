package com.digitalpayment.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VerifyOtpRequestTest {

  @Test
  void testVerifyOtpRequestConstructor() {
    VerifyOtpRequest request = new VerifyOtpRequest("test@example.com", "123456");

    assertEquals("test@example.com", request.email());
    assertEquals("123456", request.otp());
  }

  @Test
  void testVerifyOtpRequestWithNullEmail() {
    VerifyOtpRequest request = new VerifyOtpRequest(null, "123456");

    assertNull(request.email());
    assertEquals("123456", request.otp());
  }

  @Test
  void testVerifyOtpRequestWithNullOtp() {
    VerifyOtpRequest request = new VerifyOtpRequest("test@example.com", null);

    assertEquals("test@example.com", request.email());
    assertNull(request.otp());
  }

  @Test
  void testVerifyOtpRequestWithEmptyEmail() {
    VerifyOtpRequest request = new VerifyOtpRequest("", "123456");

    assertEquals("", request.email());
    assertEquals("123456", request.otp());
  }

  @Test
  void testVerifyOtpRequestWithEmptyOtp() {
    VerifyOtpRequest request = new VerifyOtpRequest("test@example.com", "");

    assertEquals("test@example.com", request.email());
    assertEquals("", request.otp());
  }

  @Test
  void testVerifyOtpRequestEquals() {
    VerifyOtpRequest request1 = new VerifyOtpRequest("test@example.com", "123456");
    VerifyOtpRequest request2 = new VerifyOtpRequest("test@example.com", "123456");

    assertEquals(request1, request2);
  }

  @Test
  void testVerifyOtpRequestNotEqualsDifferentEmail() {
    VerifyOtpRequest request1 = new VerifyOtpRequest("test@example.com", "123456");
    VerifyOtpRequest request2 = new VerifyOtpRequest("other@example.com", "123456");

    assertNotEquals(request1, request2);
  }

  @Test
  void testVerifyOtpRequestNotEqualsDifferentOtp() {
    VerifyOtpRequest request1 = new VerifyOtpRequest("test@example.com", "123456");
    VerifyOtpRequest request2 = new VerifyOtpRequest("test@example.com", "654321");

    assertNotEquals(request1, request2);
  }

  @Test
  void testVerifyOtpRequestHashCode() {
    VerifyOtpRequest request1 = new VerifyOtpRequest("test@example.com", "123456");
    VerifyOtpRequest request2 = new VerifyOtpRequest("test@example.com", "123456");

    assertEquals(request1.hashCode(), request2.hashCode());
  }

  @Test
  void testVerifyOtpRequestToString() {
    VerifyOtpRequest request = new VerifyOtpRequest("test@example.com", "123456");

    String toString = request.toString();
    assertTrue(toString.contains("test@example.com"));
    assertTrue(toString.contains("123456"));
  }

  @Test
  void testVerifyOtpRequestWithLongOtp() {
    String longOtp = "1".repeat(100);
    VerifyOtpRequest request = new VerifyOtpRequest("test@example.com", longOtp);

    assertEquals(longOtp, request.otp());
  }

  @Test
  void testVerifyOtpRequestWithSpecialCharactersInEmail() {
    VerifyOtpRequest request = new VerifyOtpRequest("test+user@example.com", "123456");

    assertEquals("test+user@example.com", request.email());
  }
}

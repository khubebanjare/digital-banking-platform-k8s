package com.digitalpayment.auth.dto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SendOtpRequestTest {

  @Test
  void testSendOtpRequestConstructor() {
    SendOtpRequest request = new SendOtpRequest("test@example.com");

    assertEquals("test@example.com", request.email());
  }

  @Test
  void testSendOtpRequestWithNullEmail() {
    SendOtpRequest request = new SendOtpRequest(null);

    assertNull(request.email());
  }

  @Test
  void testSendOtpRequestWithEmptyEmail() {
    SendOtpRequest request = new SendOtpRequest("");

    assertEquals("", request.email());
  }

  @Test
  void testSendOtpRequestEquals() {
    SendOtpRequest request1 = new SendOtpRequest("test@example.com");
    SendOtpRequest request2 = new SendOtpRequest("test@example.com");

    assertEquals(request1, request2);
  }

  @Test
  void testSendOtpRequestHashCode() {
    SendOtpRequest request1 = new SendOtpRequest("test@example.com");
    SendOtpRequest request2 = new SendOtpRequest("test@example.com");

    assertEquals(request1.hashCode(), request2.hashCode());
  }

  @Test
  void testSendOtpRequestToString() {
    SendOtpRequest request = new SendOtpRequest("test@example.com");

    String toString = request.toString();
    assertTrue(toString.contains("test@example.com"));
  }

  @Test
  void testSendOtpRequestWithSpecialCharacters() {
    SendOtpRequest request = new SendOtpRequest("test+user@example.com");

    assertEquals("test+user@example.com", request.email());
  }

  @Test
  void testSendOtpRequestWithLongEmail() {
    String longEmail = "a".repeat(100) + "@example.com";
    SendOtpRequest request = new SendOtpRequest(longEmail);

    assertEquals(longEmail, request.email());
  }
}

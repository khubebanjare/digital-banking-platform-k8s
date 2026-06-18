package com.digitalpayment.auth.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OtpEmailSendingExceptionTest {

  @Test
  void testOtpEmailSendingExceptionWithMessage() {
    OtpEmailSendingException exception = new OtpEmailSendingException("Failed to send OTP email");

    assertEquals("Failed to send OTP email", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testOtpEmailSendingExceptionWithMessageAndCause() {
    Throwable cause = new RuntimeException("SMTP error");
    OtpEmailSendingException exception =
        new OtpEmailSendingException("Failed to send OTP email", cause);

    assertEquals("Failed to send OTP email", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testOtpEmailSendingExceptionWithNullMessage() {
    OtpEmailSendingException exception = new OtpEmailSendingException(null);

    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testOtpEmailSendingExceptionWithNullCause() {
    OtpEmailSendingException exception = new OtpEmailSendingException("Error message", null);

    assertEquals("Error message", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testOtpEmailSendingExceptionIsRuntimeException() {
    OtpEmailSendingException exception = new OtpEmailSendingException("Error");

    assertTrue(exception instanceof RuntimeException);
  }

  @Test
  void testOtpEmailSendingExceptionCanBeThrown() {
    assertThrows(
        OtpEmailSendingException.class,
        () -> {
          throw new OtpEmailSendingException("Test error");
        });
  }

  @Test
  void testOtpEmailSendingExceptionWithEmptyMessage() {
    OtpEmailSendingException exception = new OtpEmailSendingException("");

    assertEquals("", exception.getMessage());
  }

  @Test
  void testOtpEmailSendingExceptionWithLongMessage() {
    String longMessage = "a".repeat(1000);
    OtpEmailSendingException exception = new OtpEmailSendingException(longMessage);

    assertEquals(longMessage, exception.getMessage());
  }

  @Test
  void testOtpEmailSendingExceptionGetCauseType() {
    Throwable cause = new IllegalArgumentException("Invalid email");
    OtpEmailSendingException exception =
        new OtpEmailSendingException("Failed to send OTP email", cause);

    assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
  }
}

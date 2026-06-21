package com.digitalpayment.auth.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmailSendingExceptionTest {

  @Test
  void testEmailSendingExceptionWithMessage() {
    EmailSendingException exception = new EmailSendingException("Failed to send email");

    assertEquals("Failed to send email", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testEmailSendingExceptionWithMessageAndCause() {
    Throwable cause = new RuntimeException("SMTP error");
    EmailSendingException exception = new EmailSendingException("Failed to send email", cause);

    assertEquals("Failed to send email", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testEmailSendingExceptionWithNullMessage() {
    EmailSendingException exception = new EmailSendingException(null);

    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testEmailSendingExceptionWithNullCause() {
    EmailSendingException exception = new EmailSendingException("Error message", null);

    assertEquals("Error message", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testEmailSendingExceptionIsRuntimeException() {
    EmailSendingException exception = new EmailSendingException("Error");

    assertTrue(exception instanceof RuntimeException);
  }

  @Test
  void testEmailSendingExceptionCanBeThrown() {
    assertThrows(
        EmailSendingException.class,
        () -> {
          throw new EmailSendingException("Test error");
        });
  }

  @Test
  void testEmailSendingExceptionWithEmptyMessage() {
    EmailSendingException exception = new EmailSendingException("");

    assertEquals("", exception.getMessage());
  }

  @Test
  void testEmailSendingExceptionWithLongMessage() {
    String longMessage = "a".repeat(1000);
    EmailSendingException exception = new EmailSendingException(longMessage);

    assertEquals(longMessage, exception.getMessage());
  }

  @Test
  void testEmailSendingExceptionGetCauseType() {
    Throwable cause = new IllegalArgumentException("Invalid email address");
    EmailSendingException exception = new EmailSendingException("Failed to send email", cause);

    assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
  }

  @Test
  void testEmailSendingExceptionWithMessagingExceptionCause() {
    jakarta.mail.MessagingException cause =
        new jakarta.mail.MessagingException("Connection refused");
    EmailSendingException exception = new EmailSendingException("Failed to send email", cause);

    assertEquals(jakarta.mail.MessagingException.class, exception.getCause().getClass());
  }
}

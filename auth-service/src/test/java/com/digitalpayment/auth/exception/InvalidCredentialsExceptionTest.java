package com.digitalpayment.auth.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidCredentialsExceptionTest {

  @Test
  void testInvalidCredentialsExceptionWithMessage() {
    InvalidCredentialsException exception =
        new InvalidCredentialsException("Invalid email or password");

    assertEquals("Invalid email or password", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testInvalidCredentialsExceptionWithMessageAndCause() {
    Throwable cause = new RuntimeException("Bad credentials");
    InvalidCredentialsException exception =
        new InvalidCredentialsException("Invalid email or password", cause);

    assertEquals("Invalid email or password", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testInvalidCredentialsExceptionWithNullMessage() {
    InvalidCredentialsException exception = new InvalidCredentialsException(null);

    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testInvalidCredentialsExceptionWithNullCause() {
    InvalidCredentialsException exception = new InvalidCredentialsException("Error message", null);

    assertEquals("Error message", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testInvalidCredentialsExceptionIsRuntimeException() {
    InvalidCredentialsException exception = new InvalidCredentialsException("Error");

    assertTrue(exception instanceof RuntimeException);
  }

  @Test
  void testInvalidCredentialsExceptionIsAuthenticationException() {
    InvalidCredentialsException exception = new InvalidCredentialsException("Error");

    assertEquals("Error", exception.getMessage());
  }

  @Test
  void testInvalidCredentialsExceptionCanBeThrown() {
    assertThrows(
        InvalidCredentialsException.class,
        () -> {
          throw new InvalidCredentialsException("Test error");
        });
  }

  @Test
  void testInvalidCredentialsExceptionWithEmptyMessage() {
    InvalidCredentialsException exception = new InvalidCredentialsException("");

    assertEquals("", exception.getMessage());
  }

  @Test
  void testInvalidCredentialsExceptionWithLongMessage() {
    String longMessage = "a".repeat(1000);
    InvalidCredentialsException exception = new InvalidCredentialsException(longMessage);

    assertEquals(longMessage, exception.getMessage());
  }

  @Test
  void testInvalidCredentialsExceptionGetCauseType() {
    Throwable cause = new IllegalArgumentException("Invalid password format");
    InvalidCredentialsException exception =
        new InvalidCredentialsException("Invalid email or password", cause);

    assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
  }
}

package com.digitalpayment.auth.exception;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AuthenticationExceptionTest {

  @Test
  void testAuthenticationExceptionWithMessage() {
    AuthenticationException exception = new AuthenticationException("Authentication failed");

    assertEquals("Authentication failed", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testAuthenticationExceptionWithMessageAndCause() {
    Throwable cause = new RuntimeException("Invalid token");
    AuthenticationException exception = new AuthenticationException("Authentication failed", cause);

    assertEquals("Authentication failed", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testAuthenticationExceptionWithNullMessage() {
    AuthenticationException exception = new AuthenticationException(null);

    assertNull(exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testAuthenticationExceptionWithNullCause() {
    AuthenticationException exception = new AuthenticationException("Error message", null);

    assertEquals("Error message", exception.getMessage());
    assertNull(exception.getCause());
  }

  @Test
  void testAuthenticationExceptionIsRuntimeException() {
    AuthenticationException exception = new AuthenticationException("Error");

    assertTrue(exception instanceof RuntimeException);
  }

  @Test
  void testAuthenticationExceptionCanBeThrown() {
    assertThrows(
        AuthenticationException.class,
        () -> {
          throw new AuthenticationException("Test error");
        });
  }

  @Test
  void testAuthenticationExceptionWithEmptyMessage() {
    AuthenticationException exception = new AuthenticationException("");

    assertEquals("", exception.getMessage());
  }

  @Test
  void testAuthenticationExceptionWithLongMessage() {
    String longMessage = "a".repeat(1000);
    AuthenticationException exception = new AuthenticationException(longMessage);

    assertEquals(longMessage, exception.getMessage());
  }

  @Test
  void testAuthenticationExceptionGetCauseType() {
    Throwable cause = new IllegalArgumentException("Invalid credentials");
    AuthenticationException exception = new AuthenticationException("Authentication failed", cause);

    assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
  }

  @Test
  void testAuthenticationExceptionWithNestedCause() {
    Throwable rootCause = new NullPointerException("Null pointer");
    Throwable cause = new RuntimeException("Wrapper", rootCause);
    AuthenticationException exception = new AuthenticationException("Authentication failed", cause);

    assertEquals(cause, exception.getCause());
    assertEquals(rootCause, exception.getCause().getCause());
  }
}

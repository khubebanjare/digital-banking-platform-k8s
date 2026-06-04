package com.digitalpayment.auth.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DuplicateEmailExceptionTest {

    @Test
    void testDuplicateEmailExceptionMessage() {
        String message = "Email already exists";
        DuplicateEmailException exception = new DuplicateEmailException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testDuplicateEmailExceptionWithCause() {
        Throwable cause = new RuntimeException("Cause");
        DuplicateEmailException exception = new DuplicateEmailException("Message", cause);

        assertEquals("Message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testDuplicateEmailExceptionIsThrowable() {
        DuplicateEmailException exception = new DuplicateEmailException("Test");
        assertNotNull(exception);
    }
}

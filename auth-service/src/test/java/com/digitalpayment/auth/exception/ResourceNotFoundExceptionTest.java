package com.digitalpayment.auth.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResourceNotFoundExceptionTest {

    @Test
    void testResourceNotFoundExceptionMessage() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testResourceNotFoundExceptionWithCause() {
        Throwable cause = new RuntimeException("Cause");
        ResourceNotFoundException exception = new ResourceNotFoundException("Message", cause);

        assertEquals("Message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testResourceNotFoundExceptionIsThrowable() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");
        assertNotNull(exception);
    }
}

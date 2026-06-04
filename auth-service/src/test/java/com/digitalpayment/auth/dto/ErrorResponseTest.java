package com.digitalpayment.auth.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorResponseTest {

    @Test
    void testErrorResponseCreation() {
        ErrorResponse response = new ErrorResponse(404, "Not Found", "Resource not found", "/api/test");

        assertEquals(404, response.getStatus());
        assertEquals("Not Found", response.getError());
        assertEquals("Resource not found", response.getMessage());
        assertEquals("/api/test", response.getPath());
    }

    @Test
    void testErrorResponseNoArgsConstructor() {
        ErrorResponse response = new ErrorResponse();
        assertNotNull(response);
    }

    @Test
    void testErrorResponseSetters() {
        ErrorResponse response = new ErrorResponse();

        response.setStatus(500);
        response.setError("Internal Server Error");
        response.setMessage("An error occurred");
        response.setPath("/api/error");

        assertEquals(500, response.getStatus());
        assertEquals("Internal Server Error", response.getError());
        assertEquals("An error occurred", response.getMessage());
        assertEquals("/api/error", response.getPath());
    }
}

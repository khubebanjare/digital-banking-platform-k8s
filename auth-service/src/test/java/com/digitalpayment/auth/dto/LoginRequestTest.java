package com.digitalpayment.auth.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class LoginRequestTest {

    @Test
    void testLoginRequestCreation() {
        LoginRequest request = new LoginRequest("john@example.com", "password123");

        assertEquals("john@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testLoginRequestNoArgsConstructor() {
        LoginRequest request = new LoginRequest();
        assertNotNull(request);
    }

    @Test
    void testLoginRequestSetters() {
        LoginRequest request = new LoginRequest();

        request.setEmail("jane@example.com");
        request.setPassword("newPassword");

        assertEquals("jane@example.com", request.getEmail());
        assertEquals("newPassword", request.getPassword());
    }
}

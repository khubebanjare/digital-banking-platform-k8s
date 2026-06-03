package com.digitalpayment.auth.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AuthResponseTest {

    @Test
    void testAuthResponseCreation() {
        UUID userId = UUID.randomUUID();
        AuthResponse response = new AuthResponse("token", "Bearer", userId, "john@example.com", "John", "Doe");

        assertEquals("token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(userId, response.getId());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
    }

    @Test
    void testAuthResponseNoArgsConstructor() {
        AuthResponse response = new AuthResponse();
        assertNotNull(response);
        assertEquals("Bearer", response.getTokenType());
    }

    @Test
    void testAuthResponseSetters() {
        AuthResponse response = new AuthResponse();
        UUID userId = UUID.randomUUID();

        response.setToken("new-token");
        response.setTokenType("Bearer");
        response.setId(userId);
        response.setEmail("jane@example.com");
        response.setFirstName("Jane");
        response.setLastName("Smith");

        assertEquals("new-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(userId, response.getId());
        assertEquals("jane@example.com", response.getEmail());
    }
}

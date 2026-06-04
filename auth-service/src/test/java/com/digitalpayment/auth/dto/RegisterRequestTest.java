package com.digitalpayment.auth.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RegisterRequestTest {

    @Test
    void testRegisterRequestCreation() {
        RegisterRequest request = new RegisterRequest("John", "Doe", "john@example.com", "password123");

        assertEquals("John", request.getFirstName());
        assertEquals("Doe", request.getLastName());
        assertEquals("john@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testRegisterRequestNoArgsConstructor() {
        RegisterRequest request = new RegisterRequest();
        assertNotNull(request);
    }

    @Test
    void testRegisterRequestSetters() {
        RegisterRequest request = new RegisterRequest();

        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("jane@example.com");
        request.setPassword("newPassword");

        assertEquals("Jane", request.getFirstName());
        assertEquals("Smith", request.getLastName());
        assertEquals("jane@example.com", request.getEmail());
        assertEquals("newPassword", request.getPassword());
    }
}

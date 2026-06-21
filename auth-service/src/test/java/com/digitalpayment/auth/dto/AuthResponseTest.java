package com.digitalpayment.auth.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.digitalpayment.auth.entity.Role;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuthResponseTest {

  @Test
  void shouldCreateAuthResponse() {

    UUID userId = UUID.randomUUID();

    AuthResponse response =
        new AuthResponse(
            "access-token",
            "refresh-token",
            "Bearer",
            86400L,
            userId,
            "john@example.com",
            Role.USER);

    assertEquals("access-token", response.accessToken());
    assertEquals("refresh-token", response.refreshToken());
    assertEquals("Bearer", response.tokenType());
    assertEquals(86400L, response.expiresIn());
    assertEquals(userId, response.userId());
    assertEquals("john@example.com", response.email());
    assertEquals(Role.USER, response.role());
  }

  @Test
  void shouldSupportEquality() {

    UUID userId = UUID.randomUUID();

    AuthResponse response1 =
        new AuthResponse(
            "access-token",
            "refresh-token",
            "Bearer",
            86400L,
            userId,
            "john@example.com",
            Role.USER);

    AuthResponse response2 =
        new AuthResponse(
            "access-token",
            "refresh-token",
            "Bearer",
            86400L,
            userId,
            "john@example.com",
            Role.USER);

    assertEquals(response1, response2);
    assertEquals(response1.hashCode(), response2.hashCode());
  }

  @Test
  void testAuthResponseCreation() {
    UUID userId = UUID.randomUUID();
    com.digitalpayment.auth.dto.AuthResponse response =
        new AuthResponse(
            "access-token",
            "refresh-token",
            "Bearer",
            86400L,
            userId,
            "john@example.com",
            Role.USER);

    assertEquals("access-token", response.accessToken());
    assertEquals("refresh-token", response.refreshToken());
    assertEquals("Bearer", response.tokenType());
    assertEquals(86400L, response.expiresIn());
    assertEquals(userId, response.userId());
    assertEquals("john@example.com", response.email());
    assertEquals(Role.USER, response.role());
  }

  @Test
  void testAuthResponseWithAdminRole() {
    UUID userId = UUID.randomUUID();
    AuthResponse response =
        new AuthResponse(
            "access-token",
            "refresh-token",
            "Bearer",
            86400L,
            userId,
            "admin@example.com",
            Role.ADMIN);

    assertEquals("access-token", response.accessToken());
    assertEquals("refresh-token", response.refreshToken());
    assertEquals("Bearer", response.tokenType());
    assertEquals(86400L, response.expiresIn());
    assertEquals(userId, response.userId());
    assertEquals("admin@example.com", response.email());
    assertEquals(Role.ADMIN, response.role());
  }

  @Test
  void testAuthResponseWithSuperAdminRole() {
    UUID userId = UUID.randomUUID();
    AuthResponse response =
        new AuthResponse(
            "access-token",
            "refresh-token",
            "Bearer",
            86400L,
            userId,
            "superadmin@example.com",
            Role.SUPER_ADMIN);

    assertEquals("access-token", response.accessToken());
    assertEquals("refresh-token", response.refreshToken());
    assertEquals("Bearer", response.tokenType());
    assertEquals(86400L, response.expiresIn());
    assertEquals(userId, response.userId());
    assertEquals("superadmin@example.com", response.email());
    assertEquals(Role.SUPER_ADMIN, response.role());
  }
}

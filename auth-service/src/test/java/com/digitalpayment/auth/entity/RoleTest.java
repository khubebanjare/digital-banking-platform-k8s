package com.digitalpayment.auth.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RoleTest {

  @Test
  void testRoleUserValue() {
    Role role = com.digitalpayment.auth.entity.Role.USER;
    assertEquals("USER", role.name());
    assertNotNull(role);
  }

  @Test
  void testRoleAdminValue() {
    Role role = Role.ADMIN;
    assertEquals("ADMIN", role.name());
    assertNotNull(role);
  }

  @Test
  void testRoleValues() {
    Role[] roles = Role.values();
    assertEquals(3, roles.length);
  }

  @Test
  void testRoleValueOf() {
    Role role = Role.valueOf("USER");
    assertEquals(Role.USER, role);
  }

  @Test
  void testRoleComparison() {
    Role user1 = Role.USER;
    Role user2 = Role.USER;
    Role admin = Role.ADMIN;

    assertEquals(user1, user2);
    assertNotEquals(user1, admin);
  }
}

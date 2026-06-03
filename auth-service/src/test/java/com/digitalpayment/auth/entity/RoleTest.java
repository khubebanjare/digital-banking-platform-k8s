package com.digitalpayment.auth.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoleTest {

    @Test
    void testRoleUserValue() {
        Role role = Role.USER;
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
        assertEquals(2, roles.length);
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

    private void assertNotEquals(Object obj1, Object obj2) {
        if (obj1.equals(obj2)) {
            throw new AssertionError("Objects should not be equal");
        }
    }
}

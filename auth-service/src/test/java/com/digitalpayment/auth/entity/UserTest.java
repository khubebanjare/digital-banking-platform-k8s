package com.digitalpayment.auth.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testUserCreation() {
        assertNotNull(user);
        assertEquals(userId, user.getId());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void testGetUsername() {
        assertEquals("john@example.com", user.getUsername());
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(user.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(user.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(user.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(user.isEnabled());
    }

    @Test
    void testUserEquality() {
        User user2 = new User();
        user2.setId(userId);
        user2.setFirstName("Jane");

        assertEquals(user, user2);
    }

    @Test
    void testUserEqualityWithNull() {
        User user2 = new User();
        user2.setId(null);

        assertNotEquals(user, user2);
    }

    @Test
    void testUserEqualityWithDifferentType() {
        assertNotEquals(user, "not a user");
    }

    @Test
    void testUserHashCode() {
        User user2 = new User();
        user2.setId(userId);

        assertEquals(user.hashCode(), user2.hashCode());
    }

    @Test
    void testUserToString() {
        String toString = user.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("User{"));
        assertTrue(toString.contains("john@example.com"));
    }

    @Test
    void testUserNoArgsConstructor() {
        User newUser = new User();
        assertNotNull(newUser);
    }

    @Test
    void testUserAllArgsConstructor() {
        User newUser = new User(userId, "Jane", "Smith", "jane@example.com", "pass", 
                Role.USER, true, true, true, true, LocalDateTime.now(), LocalDateTime.now());

        assertEquals(userId, newUser.getId());
        assertEquals("Jane", newUser.getFirstName());
        assertEquals("jane@example.com", newUser.getEmail());
    }

    @Test
    void testUserWithAdminRole() {
        user.setRole(Role.ADMIN);
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        assertTrue(authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void testUserDisabled() {
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void testUserAccountExpired() {
        user.setAccountNonExpired(false);
        assertFalse(user.isAccountNonExpired());
    }

    @Test
    void testUserAccountLocked() {
        user.setAccountNonLocked(false);
        assertFalse(user.isAccountNonLocked());
    }

    @Test
    void testUserCredentialsExpired() {
        user.setCredentialsNonExpired(false);
        assertFalse(user.isCredentialsNonExpired());
    }
}

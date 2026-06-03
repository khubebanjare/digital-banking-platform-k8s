package com.digitalpayment.auth.util;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        String secret = "mySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm";
        Long expiration = 86400000L; // 24 hours

        ReflectionTestUtils.setField(jwtUtil, "secret", secret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken("john@example.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken("john@example.com");
        String username = jwtUtil.extractUsername(token);

        assertEquals("john@example.com", username);
    }

    @Test
    void testExtractExpiration() {
        String token = jwtUtil.generateToken("john@example.com");
        Date expirationDate = jwtUtil.extractExpiration(token);

        assertNotNull(expirationDate);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void testExtractClaim() {
        String token = jwtUtil.generateToken("john@example.com");
        String username = jwtUtil.extractClaim(token, Claims::getSubject);

        assertEquals("john@example.com", username);
    }

    @Test
    void testExtractClaimWithInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(IllegalArgumentException.class, () -> 
                jwtUtil.extractClaim(invalidToken, Claims::getSubject));
    }

    @Test
    void testIsTokenExpired() {
        String token = jwtUtil.generateToken("john@example.com");
        Boolean isExpired = jwtUtil.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void testIsTokenExpiredWithNegativeExpiration() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -5000L); // negative expiration
        String token = jwtUtil.generateToken("john@example.com");
        Boolean isExpired = jwtUtil.isTokenExpired(token);
        assertTrue(isExpired);
    }

    @Test
    void testValidateTokenSuccess() {
        String token = jwtUtil.generateToken("john@example.com");
        Boolean isValid = jwtUtil.validateToken(token, "john@example.com");

        assertTrue(isValid);
    }

    @Test
    void testValidateTokenWithDifferentUsername() {
        String token = jwtUtil.generateToken("john@example.com");
        Boolean isValid = jwtUtil.validateToken(token, "jane@example.com");

        assertFalse(isValid);
    }

    @Test
    void testValidateTokenWithNullUsername() {
        String token = jwtUtil.generateToken("john@example.com");
        Boolean isValid = jwtUtil.validateToken(token, null);

        assertFalse(isValid);
    }

    @Test
    void testValidateNonExpiredToken() {
        // This test validates that a non-expired token is valid
        // The setUp method already initializes jwtUtil with 24-hour expiration
        String token = jwtUtil.generateToken("john@example.com");

        // Token should not be expired
        Boolean isExpired = jwtUtil.isTokenExpired(token);
        assertFalse(isExpired);

        // Token should be valid
        Boolean isValid = jwtUtil.validateToken(token, "john@example.com");
        assertTrue(isValid);
    }

    @Test
    void testValidateExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", 1L);
        String token = jwtUtil.generateToken("john@example.com");

        Boolean isExpired = jwtUtil.isTokenExpired(token);
        assertTrue(isExpired);

        Boolean isValid = jwtUtil.validateToken(token, "john@example.com");

        assertFalse(isValid);
    }

    @Test
    void testExtractUsernameWithInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(IllegalArgumentException.class, () -> 
                jwtUtil.extractUsername(invalidToken));
    }

    @Test
    void testExtractExpirationWithInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(IllegalArgumentException.class, () -> 
                jwtUtil.extractExpiration(invalidToken));
    }
}

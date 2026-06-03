package com.digitalpayment.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secret;
    private Long expiration;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        secret = "mySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm";
        expiration = 86400000L; // 24 hours
        
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
    void testIsTokenExpiredWithNullExpiration() {
        String token = jwtUtil.generateToken("john@example.com");
        ReflectionTestUtils.setField(jwtUtil, "expiration", -100000L); // negative expiration
        
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
    void testValidateExpiredToken() {
        String token = jwtUtil.generateToken("john@example.com");
        ReflectionTestUtils.setField(jwtUtil, "expiration", -100000L); // make token expired
        
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

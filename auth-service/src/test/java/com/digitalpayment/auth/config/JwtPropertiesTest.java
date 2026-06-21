package com.digitalpayment.auth.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtPropertiesTest {

  private JwtProperties jwtProperties;

  @BeforeEach
  void setUp() {
    jwtProperties = new JwtProperties();
  }

  @Test
  void testJwtPropertiesCreation() {
    assertNotNull(jwtProperties);
  }

  @Test
  void testSetExpiration() {
    jwtProperties.setExpiration(3600L);
    assertEquals(3600L, jwtProperties.getExpiration());
  }

  @Test
  void testSetRefreshExpiration() {
    jwtProperties.setRefreshExpiration(7200L);
    assertEquals(7200L, jwtProperties.getRefreshExpiration());
  }

  @Test
  void testSetExpirationWithZero() {
    jwtProperties.setExpiration(0L);
    assertEquals(0L, jwtProperties.getExpiration());
  }

  @Test
  void testSetRefreshExpirationWithZero() {
    jwtProperties.setRefreshExpiration(0L);
    assertEquals(0L, jwtProperties.getRefreshExpiration());
  }

  @Test
  void testSetExpirationWithNegative() {
    jwtProperties.setExpiration(-1L);
    assertEquals(-1L, jwtProperties.getExpiration());
  }

  @Test
  void testSetRefreshExpirationWithNegative() {
    jwtProperties.setRefreshExpiration(-1L);
    assertEquals(-1L, jwtProperties.getRefreshExpiration());
  }

  @Test
  void testSetExpirationWithLargeValue() {
    jwtProperties.setExpiration(Long.MAX_VALUE);
    assertEquals(Long.MAX_VALUE, jwtProperties.getExpiration());
  }

  @Test
  void testSetRefreshExpirationWithLargeValue() {
    jwtProperties.setRefreshExpiration(Long.MAX_VALUE);
    assertEquals(Long.MAX_VALUE, jwtProperties.getRefreshExpiration());
  }

  @Test
  void testJwtPropertiesDefaultValues() {
    assertEquals(0L, jwtProperties.getExpiration());
    assertEquals(0L, jwtProperties.getRefreshExpiration());
  }
}

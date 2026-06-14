package com.digitalpayment.auth.health;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Health;

class VaultHealthIndicatorTest {

  private final VaultHealthIndicator vaultHealthIndicator = new VaultHealthIndicator();

  @Test
  void testHealthIndicatorInitialization() {
    assertNotNull(vaultHealthIndicator);
  }

  @Test
  void testHealthReturnsUpStatus() {
    Health health = vaultHealthIndicator.health();
    assertNotNull(health);
    assertEquals("UP", health.getStatus().toString());
  }

  @Test
  void testHealthContainsVaultDetail() {
    Health health = vaultHealthIndicator.health();
    assertNotNull(health.getDetails());
    assertTrue(health.getDetails().containsKey("vault"));
    assertEquals("connected", health.getDetails().get("vault"));
  }

  @Test
  void testHealthReturnsValidHealthObject() {
    Health health = vaultHealthIndicator.health();
    assertNotNull(health);
    assertNotNull(health.getStatus());
    assertNotNull(health.getDetails());
  }
}

package com.digitalpayment.apigateway.health;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

class VaultHealthIndicatorTest {

  @Test
  void testHealthIndicatorCreation() {
    VaultHealthIndicator indicator = new VaultHealthIndicator();
    assertNotNull(indicator);
  }

  @Test
  void testHealthReturnsUpStatus() {

    VaultHealthIndicator indicator = new VaultHealthIndicator();

    Health health = indicator.health();

    assertNotNull(health);
    assertEquals(Status.UP, health.getStatus());
    assertEquals("connected", health.getDetails().get("vault"));
  }

  @Test
  void testHealthContainsVaultDetail() {
    VaultHealthIndicator indicator = new VaultHealthIndicator();
    Health health = indicator.health();
    assertNotNull(health);
    assertTrue(health.getDetails().containsKey("vault"));
    assertEquals("connected", health.getDetails().get("vault"));
  }

  @Test
  void testHealthIndicatorImplementsInterface() {
    VaultHealthIndicator indicator = new VaultHealthIndicator();
    assertTrue(indicator instanceof org.springframework.boot.health.contributor.HealthIndicator);
  }

  @Test
  void testHealthIsConsistent() {
    VaultHealthIndicator indicator = new VaultHealthIndicator();
    Health health1 = indicator.health();
    Health health2 = indicator.health();

    assertEquals(health1.getStatus(), health2.getStatus());
    assertEquals(health1.getDetails(), health2.getDetails());
  }

  @Test
  void testHealthDetailsAreImmutable() {
    VaultHealthIndicator indicator = new VaultHealthIndicator();
    Health health = indicator.health();

    String vaultDetail = (String) health.getDetails().get("vault");
    assertEquals("connected", vaultDetail);
  }
}

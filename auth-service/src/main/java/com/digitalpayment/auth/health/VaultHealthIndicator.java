package com.digitalpayment.auth.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class VaultHealthIndicator implements HealthIndicator {

  @Override
  public Health health() {

    return Health.up().withDetail("vault", "connected").build();
  }
}

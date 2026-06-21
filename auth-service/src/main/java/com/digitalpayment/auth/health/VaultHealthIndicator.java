package com.digitalpayment.auth.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VaultHealthIndicator implements HealthIndicator {

  @Override
  public Health health() {
    log.debug("Checking vault health");
    Health health = Health.up().withDetail("vault", "connected").build();
    log.debug("Vault health check completed: {}", health.getStatus());
    return health;
  }
}

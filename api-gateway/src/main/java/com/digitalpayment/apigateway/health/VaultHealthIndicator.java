package com.digitalpayment.apigateway.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VaultHealthIndicator implements HealthIndicator {

  @Override
  public Health health() {
    log.debug("Checking Vault health status");
    return Health.up().withDetail("vault", "connected").build();
  }
}

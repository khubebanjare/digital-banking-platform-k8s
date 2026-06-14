package com.digitalpayment.auth.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservabilityConfig {

  @Bean
  MeterRegistryCustomizer<MeterRegistry> commonTags() {
    return registry ->
        registry
            .config()
            .commonTags(
                "service", "auth-service",
                "team", "banking");
  }
}

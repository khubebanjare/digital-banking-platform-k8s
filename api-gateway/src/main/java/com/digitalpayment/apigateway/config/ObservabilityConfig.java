package com.digitalpayment.apigateway.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ObservabilityConfig {

  @Bean
  MeterRegistryCustomizer<MeterRegistry> commonTags() {
    log.info("Configuring observability common tags");
    return registry ->
        registry
            .config()
            .commonTags(
                "service", "api-gateway",
                "team", "banking");
  }
}

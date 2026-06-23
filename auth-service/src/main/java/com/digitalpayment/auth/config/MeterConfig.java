package com.digitalpayment.auth.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MeterConfig {

  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    log.info("Configuring common metrics tags");
    return registry ->
        registry
            .config()
            .commonTags(
                "service", "auth-service",
                "environment", "k8s-local");
  }
}

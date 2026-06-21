package com.digitalpayment.apigateway.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeterConfig {

  @Bean
  MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry ->
        registry
            .config()
            .commonTags(
                "service", "api-gateway",
                "environment", "k8s-local");
  }
}

package com.digitalpayment.auth.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OpenTelemetryConfigTest {

  @Test
  void testOpenTelemetryConfigCreation() {
    assertNotNull(new OpenTelemetryConfig());
  }

  @Test
  void testOpenTelemetryConfigIsConfiguration() {
    assertTrue(
        OpenTelemetryConfig.class.isAnnotationPresent(
            org.springframework.context.annotation.Configuration.class));
  }
}

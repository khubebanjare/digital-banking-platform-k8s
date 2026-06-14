package com.digitalpayment.auth.metrics;

import static org.junit.jupiter.api.Assertions.*;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;

class AuthMetricsTest {

  @Test
  void testAuthMetricsInitialization() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    AuthMetrics authMetrics = new AuthMetrics(registry);
    assertNotNull(authMetrics);
  }

  @Test
  void testGetLoginSuccessCounter() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    AuthMetrics authMetrics = new AuthMetrics(registry);
    assertNotNull(authMetrics.getLoginSuccessCounter());
  }

  @Test
  void testGetLoginFailureCounter() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    AuthMetrics authMetrics = new AuthMetrics(registry);
    assertNotNull(authMetrics.getLoginFailureCounter());
  }

  @Test
  void testGetRegistrationSuccessCounter() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    AuthMetrics authMetrics = new AuthMetrics(registry);
    assertNotNull(authMetrics.getRegistrationSuccessCounter());
  }

  @Test
  void testLoginSuccessCounterIsNotNull() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    AuthMetrics authMetrics = new AuthMetrics(registry);
    Counter counter = authMetrics.getLoginSuccessCounter();
    assertNotNull(counter);
  }

  @Test
  void testLoginFailureCounterIsNotNull() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    AuthMetrics authMetrics = new AuthMetrics(registry);
    Counter counter = authMetrics.getLoginFailureCounter();
    assertNotNull(counter);
  }

  @Test
  void testRegistrationSuccessCounterIsNotNull() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    AuthMetrics authMetrics = new AuthMetrics(registry);
    Counter counter = authMetrics.getRegistrationSuccessCounter();
    assertNotNull(counter);
  }

  @Test
  void testCountersAreDifferentInstances() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    AuthMetrics authMetrics = new AuthMetrics(registry);
    Counter loginSuccess = authMetrics.getLoginSuccessCounter();
    Counter loginFailure = authMetrics.getLoginFailureCounter();
    Counter registrationSuccess = authMetrics.getRegistrationSuccessCounter();

    assertNotSame(loginSuccess, loginFailure);
    assertNotSame(loginSuccess, registrationSuccess);
    assertNotSame(loginFailure, registrationSuccess);
  }

  @Test
  void testCountersCanIncrement() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    AuthMetrics authMetrics = new AuthMetrics(registry);

    authMetrics.getLoginSuccessCounter().increment();
    authMetrics.getLoginFailureCounter().increment();
    authMetrics.getRegistrationSuccessCounter().increment();

    assertEquals(1.0, authMetrics.getLoginSuccessCounter().count());
    assertEquals(1.0, authMetrics.getLoginFailureCounter().count());
    assertEquals(1.0, authMetrics.getRegistrationSuccessCounter().count());
  }
}

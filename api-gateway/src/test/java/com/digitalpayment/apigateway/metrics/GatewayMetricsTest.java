package com.digitalpayment.apigateway.metrics;

import static org.junit.jupiter.api.Assertions.*;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.Test;

class GatewayMetricsTest {

  @Test
  void testGatewayMetricsInitialization() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    assertNotNull(gatewayMetrics);
  }

  @Test
  void testGetRequestCounter() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    assertNotNull(gatewayMetrics.getRequestCounter());
  }

  @Test
  void testGetSuccessCounter() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    assertNotNull(gatewayMetrics.getSuccessCounter());
  }

  @Test
  void testGetFailureCounter() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    assertNotNull(gatewayMetrics.getFailureCounter());
  }

  @Test
  void testGetRateLimitExceededCounter() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    assertNotNull(gatewayMetrics.getRateLimitExceededCounter());
  }

  @Test
  void testGetAuthFailureCounter() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    assertNotNull(gatewayMetrics.getAuthFailureCounter());
  }

  @Test
  void testGetRouteNotFoundCounter() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    assertNotNull(gatewayMetrics.getRouteNotFoundCounter());
  }

  @Test
  void testGetRequestTimer() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    assertNotNull(gatewayMetrics.getRequestTimer());
  }

  @Test
  void testGetAuthTimer() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    assertNotNull(gatewayMetrics.getAuthTimer());
  }

  @Test
  void testGetRateLimiterTimer() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    assertNotNull(gatewayMetrics.getRateLimiterTimer());
  }

  @Test
  void testCountersAreDifferentInstances() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    Counter requestCounter = gatewayMetrics.getRequestCounter();
    Counter successCounter = gatewayMetrics.getSuccessCounter();
    Counter failureCounter = gatewayMetrics.getFailureCounter();

    assertNotSame(requestCounter, successCounter);
    assertNotSame(requestCounter, failureCounter);
    assertNotSame(successCounter, failureCounter);
  }

  @Test
  void testTimersAreDifferentInstances() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);
    Timer requestTimer = gatewayMetrics.getRequestTimer();
    Timer authTimer = gatewayMetrics.getAuthTimer();
    Timer rateLimiterTimer = gatewayMetrics.getRateLimiterTimer();

    assertNotSame(requestTimer, authTimer);
    assertNotSame(requestTimer, rateLimiterTimer);
    assertNotSame(authTimer, rateLimiterTimer);
  }

  @Test
  void testCountersCanIncrement() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);

    gatewayMetrics.getRequestCounter().increment();
    gatewayMetrics.getSuccessCounter().increment();
    gatewayMetrics.getFailureCounter().increment();
    gatewayMetrics.getRateLimitExceededCounter().increment();
    gatewayMetrics.getAuthFailureCounter().increment();
    gatewayMetrics.getRouteNotFoundCounter().increment();

    assertEquals(1.0, gatewayMetrics.getRequestCounter().count());
    assertEquals(1.0, gatewayMetrics.getSuccessCounter().count());
    assertEquals(1.0, gatewayMetrics.getFailureCounter().count());
    assertEquals(1.0, gatewayMetrics.getRateLimitExceededCounter().count());
    assertEquals(1.0, gatewayMetrics.getAuthFailureCounter().count());
    assertEquals(1.0, gatewayMetrics.getRouteNotFoundCounter().count());
  }

  @Test
  void testTimersCanRecord() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);

    gatewayMetrics.getRequestTimer().record(() -> {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    gatewayMetrics.getAuthTimer().record(() -> {
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    gatewayMetrics.getRateLimiterTimer().record(() -> {
      try {
        Thread.sleep(3);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    });

    assertTrue(gatewayMetrics.getRequestTimer().count() > 0);
    assertTrue(gatewayMetrics.getAuthTimer().count() > 0);
    assertTrue(gatewayMetrics.getRateLimiterTimer().count() > 0);
  }

  @Test
  void testCounterIncrementByAmount() {
    MeterRegistry registry = new io.micrometer.core.instrument.simple.SimpleMeterRegistry();
    GatewayMetrics gatewayMetrics = new GatewayMetrics(registry);

    gatewayMetrics.getRequestCounter().increment(5.0);
    gatewayMetrics.getSuccessCounter().increment(3.0);

    assertEquals(5.0, gatewayMetrics.getRequestCounter().count());
    assertEquals(3.0, gatewayMetrics.getSuccessCounter().count());
  }
}

package com.digitalpayment.apigateway.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

class RateLimiterConfigTest {

  @Test
  void testRedisRateLimiterCreation() {
    RateLimiterConfig config = new RateLimiterConfig();
    RedisRateLimiter rateLimiter = config.redisRateLimiter();
    assertNotNull(rateLimiter);
  }

  @Test
  void testIpKeyResolverCreation() {
    RateLimiterConfig config = new RateLimiterConfig();
    KeyResolver keyResolver = config.ipKeyResolver();
    assertNotNull(keyResolver);
  }

  @Test
  void testIpKeyResolverWithValidRemoteAddress() {
    RateLimiterConfig config = new RateLimiterConfig();
    KeyResolver keyResolver = config.ipKeyResolver();

    MockServerWebExchange exchange = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test")
            .remoteAddress(new InetSocketAddress("192.168.1.1", 12345))
            .build()
    );

    Mono<String> result = keyResolver.resolve(exchange);
    String ipAddress = result.block();
    assertEquals("192.168.1.1", ipAddress);
  }

  @Test
  void testIpKeyResolverWithNullRemoteAddress() {
    RateLimiterConfig config = new RateLimiterConfig();
    KeyResolver keyResolver = config.ipKeyResolver();

    MockServerWebExchange exchange = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test").build()
    );

    Mono<String> result = keyResolver.resolve(exchange);
    String ipAddress = result.block();
    assertEquals("unknown", ipAddress);
  }

  @Test
  void testIpKeyResolverWithIPv6Address() {
    RateLimiterConfig config = new RateLimiterConfig();
    KeyResolver keyResolver = config.ipKeyResolver();

    MockServerWebExchange exchange = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test")
            .remoteAddress(new InetSocketAddress("2001:db8::1", 12345))
            .build()
    );

    Mono<String> result = keyResolver.resolve(exchange);
    String ipAddress = result.block();
    assertEquals("2001:db8:0:0:0:0:0:1", ipAddress);
  }

  @Test
  void testIpKeyResolverWithLocalhost() {
    RateLimiterConfig config = new RateLimiterConfig();
    KeyResolver keyResolver = config.ipKeyResolver();

    MockServerWebExchange exchange = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test")
            .remoteAddress(new InetSocketAddress("127.0.0.1", 12345))
            .build()
    );

    Mono<String> result = keyResolver.resolve(exchange);
    String ipAddress = result.block();
    assertEquals("127.0.0.1", ipAddress);
  }

  @Test
  void testRedisRateLimiterDefaultConfiguration() {
    RateLimiterConfig config = new RateLimiterConfig();
    RedisRateLimiter rateLimiter = config.redisRateLimiter();
    assertNotNull(rateLimiter);
  }

  @Test
  void testKeyResolverReturnsMono() {
    RateLimiterConfig config = new RateLimiterConfig();
    KeyResolver keyResolver = config.ipKeyResolver();

    MockServerWebExchange exchange = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test")
            .remoteAddress(new InetSocketAddress("10.0.0.1", 8080))
            .build()
    );

    Mono<String> result = keyResolver.resolve(exchange);
    assertNotNull(result);
    assertFalse(result.toString().isEmpty());
  }

  @Test
  void testMultipleResolutionsFromSameResolver() {
    RateLimiterConfig config = new RateLimiterConfig();
    KeyResolver keyResolver = config.ipKeyResolver();

    MockServerWebExchange exchange1 = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test1")
            .remoteAddress(new InetSocketAddress("192.168.1.100", 12345))
            .build()
    );

    MockServerWebExchange exchange2 = MockServerWebExchange.from(
        org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/test2")
            .remoteAddress(new InetSocketAddress("192.168.1.200", 12345))
            .build()
    );

    String ip1 = keyResolver.resolve(exchange1).block();
    String ip2 = keyResolver.resolve(exchange2).block();

    assertEquals("192.168.1.100", ip1);
    assertEquals("192.168.1.200", ip2);
    assertNotEquals(ip1, ip2);
  }
}

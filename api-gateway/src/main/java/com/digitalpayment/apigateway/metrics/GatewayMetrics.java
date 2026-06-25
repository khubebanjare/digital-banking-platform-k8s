package com.digitalpayment.apigateway.metrics;

import com.digitalpayment.apigateway.constants.ApiGatewayConstants;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GatewayMetrics {

  private final Counter requestCounter;
  private final Counter successCounter;
  private final Counter failureCounter;
  private final Counter rateLimitExceededCounter;
  private final Counter authFailureCounter;
  private final Counter routeNotFoundCounter;

  private final Timer requestTimer;
  private final Timer authTimer;
  private final Timer rateLimiterTimer;

  public GatewayMetrics(MeterRegistry registry) {
    // Counters
    this.requestCounter =
        Counter.builder("gateway.requests.total")
            .description("Total number of requests through the gateway")
            .tag(ApiGatewayConstants.SERVICE, ApiGatewayConstants.SERVICE_NAME)
            .register(registry);

    this.successCounter =
        Counter.builder("gateway.requests.success")
            .description("Successful requests through the gateway")
            .tag(ApiGatewayConstants.SERVICE, ApiGatewayConstants.SERVICE_NAME)
            .register(registry);

    this.failureCounter =
        Counter.builder("gateway.requests.failure")
            .description("Failed requests through the gateway")
            .tag(ApiGatewayConstants.SERVICE, ApiGatewayConstants.SERVICE_NAME)
            .register(registry);

    this.rateLimitExceededCounter =
        Counter.builder("gateway.ratelimit.exceeded")
            .description("Rate limit exceeded events")
            .tag(ApiGatewayConstants.SERVICE, ApiGatewayConstants.SERVICE_NAME)
            .register(registry);

    this.authFailureCounter =
        Counter.builder("gateway.auth.failure")
            .description("Authentication failures")
            .tag(ApiGatewayConstants.SERVICE, ApiGatewayConstants.SERVICE_NAME)
            .register(registry);

    this.routeNotFoundCounter =
        Counter.builder("gateway.route.notfound")
            .description("Route not found events")
            .tag(ApiGatewayConstants.SERVICE, ApiGatewayConstants.SERVICE_NAME)
            .register(registry);

    // Timers
    this.requestTimer =
        Timer.builder("gateway.request.duration")
            .description("Request processing duration")
            .tag(ApiGatewayConstants.SERVICE, ApiGatewayConstants.SERVICE_NAME)
            .register(registry);

    this.authTimer =
        Timer.builder("gateway.auth.duration")
            .description("Authentication processing duration")
            .tag(ApiGatewayConstants.SERVICE, ApiGatewayConstants.SERVICE_NAME)
            .register(registry);

    this.rateLimiterTimer =
        Timer.builder("gateway.ratelimit.duration")
            .description("Rate limiter processing duration")
            .tag(ApiGatewayConstants.SERVICE, ApiGatewayConstants.SERVICE_NAME)
            .register(registry);
  }
}

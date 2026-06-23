package com.digitalpayment.apigateway.config;

import com.digitalpayment.apigateway.filter.ApiGatewayFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
@Slf4j
public class ApiGatewayConfig {

    private final RateLimiterConfig rateLimiterConfig;

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder, ApiGatewayFilter apiGatewayFilter) {
        log.info("Configuring API Gateway routes");
        return builder.routes()
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f
                                .requestRateLimiter(config -> {
                                    config.setKeyResolver(rateLimiterConfig.ipKeyResolver());
                                    config.setRateLimiter(rateLimiterConfig.redisRateLimiter());
                                })
                                .filter(apiGatewayFilter)
                        )
                        .uri("http://localhost:8081")
                )
                .build();
    }
}

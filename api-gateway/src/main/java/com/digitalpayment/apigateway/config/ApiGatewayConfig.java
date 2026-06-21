package com.digitalpayment.apigateway.config;

import com.digitalpayment.apigateway.filter.ApiGatewayFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class ApiGatewayConfig {

    private final RateLimiterConfig rateLimiterConfig;

    @Bean
    public RouteLocator customRouteLocator(
            RouteLocatorBuilder builder, ApiGatewayFilter apiGatewayFilter) {
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

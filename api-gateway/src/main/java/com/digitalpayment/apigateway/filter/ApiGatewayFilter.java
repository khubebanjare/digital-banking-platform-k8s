package com.digitalpayment.apigateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ApiGatewayFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getRequest()
                .mutate()
                .header("X-API-GATEWAY", "ApiGatewayFilter")
                .header("X-Custom-Header", "MyValue")
                .build();

        return chain.filter(exchange).then(
                Mono.fromRunnable(() ->
                        log.info("Response status: {}", exchange.getResponse().getStatusCode())
                ));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

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
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-API-GATEWAY", "ApiGatewayFilter")
                        .header("X-Custom-Header", "MyValue")
                        .build())
                .build();

        return chain.filter(mutatedExchange).then(
                Mono.fromRunnable(() ->
                        log.info("Response status: {}", mutatedExchange.getResponse().getStatusCode())
                ));
    }

    @Override
    public int getOrder() {
        return 1;
    }
}

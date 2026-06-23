package com.digitalpayment.apigateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@Configuration
@Slf4j
public class RateLimiterConfig {

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        log.info("Configuring Redis rate limiter with 2 requests per second, burst of 5");
        return new RedisRateLimiter(2, 5);
    }

    @Bean
    public KeyResolver ipKeyResolver() {
        log.info("Configuring IP-based key resolver for rate limiting");
        return exchange -> {

            InetSocketAddress remoteAddress =
                    exchange.getRequest().getRemoteAddress();

            if (remoteAddress == null) {
                log.warn("Remote address is null, using 'unknown' as key");
                return Mono.just("unknown");
            }

            return Mono.just(
                    remoteAddress.getAddress().getHostAddress()
            );
        };
    }
}

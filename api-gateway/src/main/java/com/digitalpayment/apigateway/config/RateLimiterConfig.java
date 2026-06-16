//package com.digitalpayment.apigateway.config;
//
//import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import reactor.core.publisher.Mono;
//
//import java.net.InetSocketAddress;
//import java.security.Principal;
//
//@Configuration
//public class RateLimiterConfig {
//
//    @Bean
//    public KeyResolver userKeyResolver() {
//
//        return exchange ->
//                exchange.getPrincipal()
//                        .map(Principal::getName)
//                        .defaultIfEmpty("anonymous");
//    }
//
//    @Bean
//    public KeyResolver ipKeyResolver() {
//
//        return exchange -> {
//
//            InetSocketAddress remoteAddress =
//                    exchange.getRequest().getRemoteAddress();
//
//            if (remoteAddress == null) {
//                return Mono.just("unknown");
//            }
//
//            return Mono.just(
//                    remoteAddress.getAddress().getHostAddress()
//            );
//        };
//    }
//}

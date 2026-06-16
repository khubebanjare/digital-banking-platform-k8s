//package com.digitalpayment.apigateway.config;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
//import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//
//@RequiredArgsConstructor
//@Configuration
//@EnableWebFluxSecurity
//public class SecurityConfig {
//
//    private final VaultProperties vaultProperties;
//    @Bean
//    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
//
//        return http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//
//                .authorizeExchange(exchanges -> exchanges
//
//                        .pathMatchers(
//                                "/api/auth/login",
//                                "/api/auth/register",
//                                "/actuator/health"
//                        ).permitAll()
//
//                        .anyExchange()
//                        .authenticated()
//                )
//
//                .oauth2ResourceServer(
//                        oauth2 -> oauth2.jwt(Customizer.withDefaults())
//                )
//
//                .build();
//    }
//
//    @Bean
//    ReactiveJwtDecoder jwtDecoder() {
//        SecretKey key =
//                new SecretKeySpec(
//                        vaultProperties.getSecret().getBytes(StandardCharsets.UTF_8),
//                        "HmacSHA256"
//                );
//
//        return NimbusReactiveJwtDecoder
//                .withSecretKey(key)
//                .build();
//    }
//}
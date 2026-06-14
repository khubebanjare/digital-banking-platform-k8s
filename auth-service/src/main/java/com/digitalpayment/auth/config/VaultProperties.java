package com.digitalpayment.auth.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class VaultProperties {
    private String secret;

    @PostConstruct
    public void init(){
        log.info("VaultProperties initialized with secret: {}", secret);
    }
}
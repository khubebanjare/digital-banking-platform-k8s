package com.digitalpayment.auth.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "jwt")
public class VaultProperties {
  private String secret;

  @Value("${spring.mail.host}")
  private String host;

  @Value("${spring.mail.username}")
  private String username;

  @Value("${spring.mail.password}")
  private String password;

  @PostConstruct
  public void init() {
    log.info("SMTP Host: {}", host);
    log.info("SMTP User: {}", username);
    log.info("SMTP Password: {}", password);
  }
}

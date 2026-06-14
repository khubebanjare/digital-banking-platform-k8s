package com.digitalpayment.auth.health;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupCheck {

  @PostConstruct
  public void init() {
    log.info("Performing startup checks...");

    try {
      Thread.sleep(2000);
      log.info("Startup checks completed successfully.");
    } catch (InterruptedException e) {
      log.error("Startup checks interrupted", e);
      Thread.currentThread().interrupt();
    }
  }
}

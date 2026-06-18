package com.digitalpayment.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Health check endpoints")
@Slf4j
public class HealthController {

  @GetMapping
  @Operation(summary = "Health check endpoint")
  public ResponseEntity<Map<String, Object>> health() {
    log.debug("Health check requested");
    Map<String, Object> response = new HashMap<>();
    response.put("status", "UP");
    response.put("service", "auth-service");
    log.debug("Health check completed");
    return ResponseEntity.ok(response);
  }
}

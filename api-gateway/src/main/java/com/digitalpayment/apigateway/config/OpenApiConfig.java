package com.digitalpayment.apigateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    log.info("Configuring OpenAPI documentation");
    return new OpenAPI()
        .info(
            new Info()
                .title("API Gateway")
                .version("1.0")
                .description("API Gateway for routing and securing microservices"));
  }
}

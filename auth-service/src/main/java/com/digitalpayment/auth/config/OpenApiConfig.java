package com.digitalpayment.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    log.info("Configuring OpenAPI documentation for Auth Service");
    return new OpenAPI()
        .info(
            new Info()
                .title("Auth Service API")
                .version("1.0")
                .description("Authentication and Authorization Service API"))
        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
        .components(
            new io.swagger.v3.oas.models.Components()
                .addSecuritySchemes(
                    "Bearer Authentication",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }
}

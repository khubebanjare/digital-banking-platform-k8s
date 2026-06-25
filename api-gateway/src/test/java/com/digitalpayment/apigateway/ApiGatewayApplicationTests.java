package com.digitalpayment.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ComponentScan(
    basePackages = "com.digitalpayment.apigateway",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = com.digitalpayment.apigateway.config.ApiGatewayConfig.class
    )
)
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key",
    "spring.config.import=optional:configserver:,optional:vault:",
    "spring.cloud.config.enabled=false",
    "spring.cloud.vault.enabled=false",
    "grafana.otlp.endpoint=http://localhost:4318",
    "grafana.otlp.auth=none"
})
class ApiGatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}

package com.digitalpayment.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    void testCustomOpenAPI() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertNotNull(openAPI);
        
        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("Auth Service API", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertEquals("Authentication and Authorization Service API", info.getDescription());
    }

    @Test
    void testCustomOpenAPISecurityRequirements() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertNotNull(openAPI.getSecurity());
        assertFalse(openAPI.getSecurity().isEmpty());
        
        SecurityRequirement securityRequirement = openAPI.getSecurity().getFirst();
        assertNotNull(securityRequirement);
        assertTrue(securityRequirement.containsKey("Bearer Authentication"));
    }

    @Test
    void testCustomOpenAPISecuritySchemes() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());
        
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("Bearer Authentication");
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
    }

    @Test
    void testCustomOpenAPICompleteStructure() {
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getSecurity());
        
        assertEquals("Auth Service API", openAPI.getInfo().getTitle());
        assertEquals("1.0", openAPI.getInfo().getVersion());
        assertEquals("Authentication and Authorization Service API", openAPI.getInfo().getDescription());
        
        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("Bearer Authentication");
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
        
        assertTrue(openAPI.getSecurity().getFirst().containsKey("Bearer Authentication"));
    }
}

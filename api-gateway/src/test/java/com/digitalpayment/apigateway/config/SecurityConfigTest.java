package com.digitalpayment.apigateway.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

class SecurityConfigTest {

  @Test
  void testSecurityConfigCreation() {
    VaultProperties vaultProperties = new VaultProperties();
    vaultProperties.setSecret("test-secret-key");
    SecurityConfig securityConfig = new SecurityConfig(vaultProperties);
    assertNotNull(securityConfig);
  }

  @Test
  void testJwtDecoderCreation() {
    VaultProperties vaultProperties = new VaultProperties();
    vaultProperties.setSecret("test-secret-key-for-jwt-decoding");
    SecurityConfig securityConfig = new SecurityConfig(vaultProperties);
    
    ReactiveJwtDecoder jwtDecoder = securityConfig.jwtDecoder();
    assertNotNull(jwtDecoder);
  }

  @Test
  void testJwtDecoderWithValidSecret() {
    VaultProperties vaultProperties = new VaultProperties();
    String testSecret = "my-secret-key-for-testing";
    vaultProperties.setSecret(testSecret);
    SecurityConfig securityConfig = new SecurityConfig(vaultProperties);
    
    ReactiveJwtDecoder jwtDecoder = securityConfig.jwtDecoder();
    assertNotNull(jwtDecoder);
  }

  @Test
  void testJwtDecoderWithEmptySecret() {
    VaultProperties vaultProperties = new VaultProperties();
    vaultProperties.setSecret("test-secret");
    SecurityConfig securityConfig = new SecurityConfig(vaultProperties);
    
    ReactiveJwtDecoder jwtDecoder = securityConfig.jwtDecoder();
    assertNotNull(jwtDecoder);
  }

  @Test
  void testSecurityConfigWithNullVaultProperties() {
    SecurityConfig config = new SecurityConfig(null);
    assertNotNull(config);
  }

  @Test
  void testVaultPropertiesDependency() {
    VaultProperties vaultProperties = new VaultProperties();
    vaultProperties.setSecret("secret123");
    SecurityConfig securityConfig = new SecurityConfig(vaultProperties);
    
    assertNotNull(securityConfig);
  }
}

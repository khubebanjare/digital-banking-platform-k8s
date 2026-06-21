package com.digitalpayment.auth.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VaultPropertiesTest {

  private VaultProperties vaultProperties;

  @BeforeEach
  void setUp() {
    vaultProperties = new VaultProperties();
  }

  @Test
  void testVaultPropertiesCreation() {
    assertNotNull(vaultProperties);
  }

  @Test
  void testSetSecret() {
    vaultProperties.setSecret("my-secret-key");
    assertEquals("my-secret-key", vaultProperties.getSecret());
  }

  @Test
  void testSetSecretWithNull() {
    vaultProperties.setSecret(null);
    assertEquals(null, vaultProperties.getSecret());
  }

  @Test
  void testSetSecretWithEmptyString() {
    vaultProperties.setSecret("");
    assertEquals("", vaultProperties.getSecret());
  }

  @Test
  void testSetSecretWithLongString() {
    String longSecret = "a".repeat(1000);
    vaultProperties.setSecret(longSecret);
    assertEquals(longSecret, vaultProperties.getSecret());
  }

  @Test
  void testSetSecretWithSpecialCharacters() {
    String specialSecret = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    vaultProperties.setSecret(specialSecret);
    assertEquals(specialSecret, vaultProperties.getSecret());
  }

  @Test
  void testVaultPropertiesDefaultValue() {
    assertEquals(null, vaultProperties.getSecret());
  }
}

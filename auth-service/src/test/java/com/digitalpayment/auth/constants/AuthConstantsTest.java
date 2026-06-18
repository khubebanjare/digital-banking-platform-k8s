package com.digitalpayment.auth.constants;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class AuthConstantsTest {

  @Test
  void testTokenPrefixConstant() {
    assertEquals("Bearer", AuthConstants.TOKEN_PREFIX);
  }

  @Test
  void testAuthConstantsClassIsPrivateConstructor() throws NoSuchMethodException {
    Constructor<AuthConstants> constructor = AuthConstants.class.getDeclaredConstructor();
    assertTrue(Modifier.isPrivate(constructor.getModifiers()));
  }

  @Test
  void testAuthConstantsHasOnlyStaticMembers() {
    boolean hasOnlyStatic = true;
    for (var field : AuthConstants.class.getDeclaredFields()) {
      if (!Modifier.isStatic(field.getModifiers())) {
        hasOnlyStatic = false;
        break;
      }
    }
    assertTrue(hasOnlyStatic);
  }

  @Test
  void testTokenPrefixIsNotNull() {
    assertNotNull(AuthConstants.TOKEN_PREFIX);
  }

  @Test
  void testTokenPrefixIsNotEmpty() {
    assertFalse(AuthConstants.TOKEN_PREFIX.isEmpty());
  }

  @Test
  void testTokenPrefixIsString() {
    assertTrue(AuthConstants.TOKEN_PREFIX instanceof String);
  }
}

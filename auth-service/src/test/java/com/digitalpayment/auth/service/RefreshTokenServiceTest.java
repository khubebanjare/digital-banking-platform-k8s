package com.digitalpayment.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import com.digitalpayment.auth.entity.RefreshToken;
import com.digitalpayment.auth.entity.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RefreshTokenServiceTest {

  @Test
  void testRefreshTokenServiceInterfaceExists() {
    assertNotNull(RefreshTokenService.class);
    assertTrue(RefreshTokenService.class.isInterface());
  }

  @Test
  void testRefreshTokenServiceHasCreateRefreshTokenMethod() throws NoSuchMethodException {
    assertNotNull(RefreshTokenService.class.getMethod("createRefreshToken", User.class));
  }

  @Test
  void testRefreshTokenServiceHasFindByTokenMethod() throws NoSuchMethodException {
    assertNotNull(RefreshTokenService.class.getMethod("findByToken", String.class));
  }

  @Test
  void testRefreshTokenServiceHasVerifyExpirationMethod() throws NoSuchMethodException {
    assertNotNull(RefreshTokenService.class.getMethod("verifyExpiration", RefreshToken.class));
  }

  @Test
  void testRefreshTokenServiceHasDeleteByUserMethod() throws NoSuchMethodException {
    assertNotNull(RefreshTokenService.class.getMethod("deleteByUser", User.class));
  }

  @Test
  void testRefreshTokenServiceHasRevokeTokenMethod() throws NoSuchMethodException {
    assertNotNull(RefreshTokenService.class.getMethod("revokeToken", String.class));
  }

  @Test
  void testRefreshTokenServiceCreateRefreshTokenReturnsRefreshToken() throws NoSuchMethodException {
    var method = RefreshTokenService.class.getMethod("createRefreshToken", User.class);
    assertEquals(RefreshToken.class, method.getReturnType());
  }

  @Test
  void testRefreshTokenServiceFindByTokenReturnsOptional() throws NoSuchMethodException {
    var method = RefreshTokenService.class.getMethod("findByToken", String.class);
    assertEquals(Optional.class, method.getReturnType());
  }

  @Test
  void testRefreshTokenServiceVerifyExpirationReturnsRefreshToken() throws NoSuchMethodException {
    var method = RefreshTokenService.class.getMethod("verifyExpiration", RefreshToken.class);
    assertEquals(RefreshToken.class, method.getReturnType());
  }

  @Test
  void testRefreshTokenServiceDeleteByUserReturnsVoid() throws NoSuchMethodException {
    var method = RefreshTokenService.class.getMethod("deleteByUser", User.class);
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  void testRefreshTokenServiceRevokeTokenReturnsVoid() throws NoSuchMethodException {
    var method = RefreshTokenService.class.getMethod("revokeToken", String.class);
    assertEquals(void.class, method.getReturnType());
  }
}

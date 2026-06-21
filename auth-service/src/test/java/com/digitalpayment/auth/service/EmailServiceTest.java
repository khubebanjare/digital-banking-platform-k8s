package com.digitalpayment.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EmailServiceTest {

  @Test
  void testEmailServiceInterfaceExists() {
    assertNotNull(EmailService.class);
    assertTrue(EmailService.class.isInterface());
  }

  @Test
  void testEmailServiceHasSendPasswordResetEmailMethod() throws NoSuchMethodException {
    assertNotNull(
        EmailService.class.getMethod("sendPasswordResetEmail", String.class, String.class));
  }

  @Test
  void testEmailServiceMethodThrowsMessagingException() throws NoSuchMethodException {
    var method = EmailService.class.getMethod("sendPasswordResetEmail", String.class, String.class);
    assertTrue(method.getExceptionTypes().length > 0);
    assertEquals("jakarta.mail.MessagingException", method.getExceptionTypes()[0].getName());
  }
}

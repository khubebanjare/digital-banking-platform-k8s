package com.digitalpayment.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

  @Mock private JavaMailSender mailSender;

  @Mock private MimeMessage mimeMessage;

  @InjectMocks private com.digitalpayment.auth.service.impl.EmailServiceImpl emailServiceImpl;

  @BeforeEach
  void setUp() {
    lenient()
        .when(mailSender.createMimeMessage())
        .thenAnswer(invocation -> new MimeMessage(Session.getInstance(new Properties())));
  }

  @Test
  void testSendPasswordResetEmail() throws MessagingException, UnsupportedEncodingException {

    String to = "test@example.com";

    String resetLink = "http://example.com/reset?token=abc123";

    emailServiceImpl.sendPasswordResetEmail(to, resetLink);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("Reset Password", capturedMessage.getSubject());
    assertEquals(to, capturedMessage.getAllRecipients()[0].toString());
  }

  @Test
  void testSendPasswordResetEmailWithEmptyResetLink()
      throws MessagingException, UnsupportedEncodingException {

    String to = "test@example.com";

    String resetLink = "";

    emailServiceImpl.sendPasswordResetEmail(to, resetLink);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("Reset Password", capturedMessage.getSubject());
    assertEquals(to, capturedMessage.getAllRecipients()[0].toString());
  }

  @Test
  void testSendPasswordResetEmailWithLongResetLink()
      throws MessagingException, UnsupportedEncodingException {
    String to = "test@example.com";
    String resetLink = "http://example.com/reset?token=" + "a".repeat(1000);

    emailServiceImpl.sendPasswordResetEmail(to, resetLink);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("Reset Password", capturedMessage.getSubject());
    assertEquals(to, capturedMessage.getAllRecipients()[0].toString());
  }

  @Test
  void testSendPasswordResetEmailWithSpecialCharactersInLink()
      throws MessagingException, UnsupportedEncodingException {

    String to = "test@example.com";

    String resetLink = "http://example.com/reset?token=abc-123_xyz&param=value";

    emailServiceImpl.sendPasswordResetEmail(to, resetLink);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("Reset Password", capturedMessage.getSubject());
    assertEquals(to, capturedMessage.getAllRecipients()[0].toString());
  }

  @Test
  void testEmailServiceImplImplementsEmailService() {
    assertTrue(emailServiceImpl instanceof com.digitalpayment.auth.service.EmailService);
  }

  @Test
  void testEmailServiceImplIsService() {
    assertTrue(
        emailServiceImpl
            .getClass()
            .isAnnotationPresent(org.springframework.stereotype.Service.class));
  }

  @Test
  void testSendOtpEmail() throws MessagingException, UnsupportedEncodingException {

    String email = "test@example.com";
    String otp = "123456";

    emailServiceImpl.sendOtpEmail(email, otp);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("OTP Verification Code", capturedMessage.getSubject());
    assertEquals(email, capturedMessage.getAllRecipients()[0].toString());
  }

  @Test
  void testSendOtpEmailWithEmptyOtp() throws MessagingException, UnsupportedEncodingException {

    String email = "test@example.com";
    String otp = "";

    emailServiceImpl.sendOtpEmail(email, otp);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("OTP Verification Code", capturedMessage.getSubject());
    assertEquals(email, capturedMessage.getAllRecipients()[0].toString());
  }

  @Test
  void testSendOtpEmailWithLongOtp() throws MessagingException, UnsupportedEncodingException {
    String email = "test@example.com";
    String otp = "1".repeat(100);

    emailServiceImpl.sendOtpEmail(email, otp);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("OTP Verification Code", capturedMessage.getSubject());
    assertEquals(email, capturedMessage.getAllRecipients()[0].toString());
  }

  @Test
  void testSendOtpEmailWithSpecialCharactersInEmail()
      throws MessagingException, UnsupportedEncodingException {
    String email = "test+user@example.com";
    String otp = "123456";

    emailServiceImpl.sendOtpEmail(email, otp);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("OTP Verification Code", capturedMessage.getSubject());
    assertEquals(email, capturedMessage.getAllRecipients()[0].toString());
  }

  @Test
  void testSendVerificationEmail() throws MessagingException, UnsupportedEncodingException {
    String email = "test@example.com";
    String verificationLink = "http://example.com/verify?token=abc123";

    emailServiceImpl.sendVerificationEmail(email, verificationLink);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("Verify Your Email Address", capturedMessage.getSubject());
  }

  @Test
  void testSendVerificationEmailWithEmptyLink()
      throws MessagingException, UnsupportedEncodingException {
    String email = "test@example.com";
    String verificationLink = "";

    emailServiceImpl.sendVerificationEmail(email, verificationLink);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("Verify Your Email Address", capturedMessage.getSubject());
    assertEquals(email, capturedMessage.getAllRecipients()[0].toString());
  }

  @Test
  void testSendVerificationEmailWithLongLink()
      throws MessagingException, UnsupportedEncodingException {
    String email = "test@example.com";
    String verificationLink = "http://example.com/verify?token=" + "a".repeat(1000);

    emailServiceImpl.sendVerificationEmail(email, verificationLink);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("Verify Your Email Address", capturedMessage.getSubject());
    assertEquals(email, capturedMessage.getAllRecipients()[0].toString());
  }

  @Test
  void testSendVerificationEmailWithSpecialCharactersInLink()
      throws MessagingException, UnsupportedEncodingException {
    String email = "test@example.com";
    String verificationLink = "http://example.com/verify?token=abc-123_xyz&param=value";

    emailServiceImpl.sendVerificationEmail(email, verificationLink);

    verify(mailSender).createMimeMessage();
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender).send(messageCaptor.capture());
    MimeMessage capturedMessage = messageCaptor.getValue();
    assertEquals("Verify Your Email Address", capturedMessage.getSubject());
    assertEquals(email, capturedMessage.getAllRecipients()[0].toString());
  }
}

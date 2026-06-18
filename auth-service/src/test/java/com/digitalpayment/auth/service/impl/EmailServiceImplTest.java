package com.digitalpayment.auth.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
  }

  @Test
  void testSendPasswordResetEmail() throws MessagingException {

    String to = "test@example.com";

    String resetLink = "http://example.com/reset?token=abc123";

    MimeMessage mimeMessageLocal = new MimeMessage(Session.getInstance(new Properties()));

    when(mailSender.createMimeMessage()).thenReturn(mimeMessageLocal);

    emailServiceImpl.sendPasswordResetEmail(to, resetLink);

    verify(mailSender).createMimeMessage();

    verify(mailSender).send(mimeMessageLocal);
  }

  @Test
  void testSendPasswordResetEmailWithEmptyResetLink() throws MessagingException {

    String to = "test@example.com";

    String resetLink = "";

    MimeMessage mimeMessageLocal = new MimeMessage(Session.getInstance(new Properties()));

    when(mailSender.createMimeMessage()).thenReturn(mimeMessageLocal);

    emailServiceImpl.sendPasswordResetEmail(to, resetLink);

    verify(mailSender).createMimeMessage();

    verify(mailSender).send(mimeMessageLocal);
  }

  @Test
  void testSendPasswordResetEmailWithLongResetLink() throws MessagingException {
    String to = "test@example.com";
    String resetLink = "http://example.com/reset?token=" + "a".repeat(1000);

    emailServiceImpl.sendPasswordResetEmail(to, resetLink);

    verify(mailSender).createMimeMessage();
    verify(mailSender).send(any(MimeMessage.class));
  }

  @Test
  void testSendPasswordResetEmailWithSpecialCharactersInLink() throws MessagingException {

    String to = "test@example.com";

    String resetLink = "http://example.com/reset?token=abc-123_xyz&param=value";

    MimeMessage mimeMessageLocal = new MimeMessage(Session.getInstance(new Properties()));

    when(mailSender.createMimeMessage()).thenReturn(mimeMessageLocal);

    emailServiceImpl.sendPasswordResetEmail(to, resetLink);

    verify(mailSender).createMimeMessage();

    verify(mailSender).send(mimeMessageLocal);
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
  void testSendOtpEmail() throws MessagingException {

    String email = "test@example.com";
    String otp = "123456";

    MimeMessage mimeMessageLocal = new MimeMessage(Session.getInstance(new Properties()));

    when(mailSender.createMimeMessage()).thenReturn(mimeMessageLocal);

    emailServiceImpl.sendOtpEmail(email, otp);

    verify(mailSender).createMimeMessage();

    verify(mailSender).send(mimeMessageLocal);
  }

  @Test
  void testSendOtpEmailWithEmptyOtp() throws MessagingException {

    String email = "test@example.com";
    String otp = "";

    MimeMessage mimeMessageLocal = new MimeMessage(Session.getInstance(new Properties()));

    when(mailSender.createMimeMessage()).thenReturn(mimeMessageLocal);

    emailServiceImpl.sendOtpEmail(email, otp);

    verify(mailSender).createMimeMessage();

    verify(mailSender).send(mimeMessageLocal);
  }

  @Test
  void testSendOtpEmailWithLongOtp() throws MessagingException {
    String email = "test@example.com";
    String otp = "1".repeat(100);

    emailServiceImpl.sendOtpEmail(email, otp);

    verify(mailSender).createMimeMessage();
    verify(mailSender).send(any(MimeMessage.class));
  }

  @Test
  void testSendOtpEmailWithSpecialCharactersInEmail() throws MessagingException {
    String email = "test+user@example.com";
    String otp = "123456";

    emailServiceImpl.sendOtpEmail(email, otp);

    verify(mailSender).createMimeMessage();
    verify(mailSender).send(any(MimeMessage.class));
  }
}

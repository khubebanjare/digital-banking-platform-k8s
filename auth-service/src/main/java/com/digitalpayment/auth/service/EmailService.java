package com.digitalpayment.auth.service;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailService {
  void sendPasswordResetEmail(String to, String resetLink)
      throws MessagingException, UnsupportedEncodingException;

  void sendOtpEmail(String email, String otp)
      throws MessagingException, UnsupportedEncodingException;

  void sendVerificationEmail(String email, String verificationLink)
      throws MessagingException, UnsupportedEncodingException;
}

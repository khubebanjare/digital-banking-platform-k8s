package com.digitalpayment.auth.service;

import jakarta.mail.MessagingException;

public interface EmailService {
  void sendPasswordResetEmail(String to, String resetLink) throws MessagingException;

  void sendOtpEmail(String email, String otp) throws MessagingException;
}

package com.digitalpayment.auth.service.impl;

import com.digitalpayment.auth.constants.AuthConstants;
import com.digitalpayment.auth.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;

  @Override
  public void sendPasswordResetEmail(String to, String resetLink)
      throws MessagingException, UnsupportedEncodingException {
    log.info("Sending password reset email to: {}", to);
    MimeMessage message = mailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setFrom(AuthConstants.FROM_EMAIL, AuthConstants.FROM_NAME);

    helper.setTo(to);

    helper.setSubject("Reset Password");

    helper.setText(
        """
                <html>
                  <body>
                    <h2>Password Reset Request</h2>
                    <p>Click below:</p>

                    <a href="%s">
                      Reset Password
                    </a>

                  </body>
                </html>
                """
            .formatted(resetLink),
        true);

    mailSender.send(message);
    log.info("Password reset email sent successfully to: {}", to);
  }

  @Override
  public void sendOtpEmail(String email, String otp)
      throws MessagingException, UnsupportedEncodingException {
    log.info("Sending OTP email to: {}", email);
    MimeMessage message = mailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

    helper.setFrom(AuthConstants.FROM_EMAIL, AuthConstants.FROM_NAME);
    helper.setTo(email);

    helper.setSubject("OTP Verification Code");

    String htmlContent =
        """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">

                <div style="
                        max-width: 600px;
                        margin: auto;
                        background: white;
                        padding: 30px;
                        border-radius: 10px;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.1);">

                    <h2 style="color: #333;">
                        OTP Verification
                    </h2>

                    <p>
                        Hello,
                    </p>

                    <p>
                        Use the following One-Time Password (OTP)
                        to verify your account:
                    </p>

                    <div style="
                            text-align:center;
                            margin:30px 0;">

                        <span style="
                                font-size:32px;
                                font-weight:bold;
                                letter-spacing:5px;
                                color:#2563eb;">
                            %s
                        </span>
                    </div>

                    <p>
                        This OTP is valid for
                        <strong>5 minutes</strong>.
                    </p>

                    <p>
                        If you did not request this OTP,
                        please ignore this email.
                    </p>

                    <hr>

                    <p style="
                            font-size:12px;
                            color:#666;">
                        Digital Payment Team
                    </p>

                </div>

            </body>
            </html>
            """
            .formatted(otp);

    helper.setText(htmlContent, true);

    mailSender.send(message);
    log.info("OTP email sent successfully to: {}", email);
  }

  @Override
  public void sendVerificationEmail(String email, String verificationLink)
      throws MessagingException, UnsupportedEncodingException {
    log.info("Sending verification email to: {}", email);
    MimeMessage message = mailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

    helper.setFrom(AuthConstants.FROM_EMAIL, AuthConstants.FROM_NAME);
    helper.setTo(email);

    helper.setSubject("Verify Your Email Address");

    String html =
        """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif;
                         background:#f4f4f4;
                         padding:40px;">

                <div style="
                    max-width:600px;
                    margin:auto;
                    background:white;
                    padding:30px;
                    border-radius:12px;
                    box-shadow:0 2px 10px rgba(0,0,0,0.1);">

                    <h2 style="color:#2563eb;">
                        Welcome to Digital Payment
                    </h2>

                    <p>
                        Thank you for registering.
                    </p>

                    <p>
                        Please verify your email address
                        by clicking the button below.
                    </p>

                    <div style="text-align:center;
                                margin:30px 0;">

                        <a href="%s"
                           style="
                           background:#2563eb;
                           color:white;
                           text-decoration:none;
                           padding:14px 24px;
                           border-radius:8px;
                           font-weight:bold;">
                           Verify Email
                        </a>

                    </div>

                    <p>
                        This verification link will expire
                        in 24 hours.
                    </p>

                    <p>
                        If you did not create this account,
                        please ignore this email.
                    </p>

                    <hr>

                    <p style="
                        font-size:12px;
                        color:#777;">
                        Digital Payment Team
                    </p>

                </div>

            </body>
            </html>
            """
            .formatted(verificationLink);

    helper.setText(html, true);

    mailSender.send(message);
    log.info("Verification email sent successfully to: {}", email);
  }
}

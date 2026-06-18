package com.digitalpayment.auth.exception;

public class OtpEmailSendingException extends RuntimeException {

  public OtpEmailSendingException(String message) {
    super(message);
  }

  public OtpEmailSendingException(String message, Throwable cause) {
    super(message, cause);
  }
}

package com.digitalpayment.auth.exception;

public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

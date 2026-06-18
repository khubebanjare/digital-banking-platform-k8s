package com.digitalpayment.auth.exception;

import com.digitalpayment.auth.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
      ResourceNotFoundException ex, org.springframework.web.context.request.WebRequest request) {
    log.warn("Resource not found: {}", ex.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""));
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateEmailException(
      DuplicateEmailException ex, org.springframework.web.context.request.WebRequest request) {
    log.warn("Duplicate email attempt: {}", ex.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""));
    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
      UsernameNotFoundException ex, org.springframework.web.context.request.WebRequest request) {
    log.warn("Username not found: {}", ex.getMessage());
    ErrorResponse errorResponse =
        new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""));
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentialsException(
      BadCredentialsException ex, org.springframework.web.context.request.WebRequest request) {
    log.warn("Bad credentials attempt");
    ErrorResponse errorResponse =
        new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            "Invalid email or password",
            request.getDescription(false).replace("uri=", ""));
    return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    log.warn("Validation failed: {}", ex.getMessage());
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (ObjectError error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    Map<String, Object> response = new HashMap<>();
    response.put("timestamp", java.time.LocalDateTime.now());
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Validation Failed");
    response.put("errors", errors);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      Exception ex, org.springframework.web.context.request.WebRequest request) {
    log.error("Unexpected error occurred", ex);
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    String errorType = "Internal Server Error";

    if (ex instanceof IllegalArgumentException) {
      status = HttpStatus.BAD_REQUEST;
      errorType = "Invalid Request";
    }

    ErrorResponse errorResponse =
        new ErrorResponse(
            status.value(),
            errorType,
            ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred",
            request.getDescription(false).replace("uri=", ""));
    return new ResponseEntity<>(errorResponse, status);
  }

  @ExceptionHandler(EmailSendingException.class)
  public ResponseEntity<ErrorResponse> handleEmailSendingException(
      EmailSendingException ex, HttpServletRequest request) {

    ErrorResponse response =
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Email Sending Failed",
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }

  @ExceptionHandler(OtpEmailSendingException.class)
  public ResponseEntity<ErrorResponse> handleOtpEmailSendingException(
      OtpEmailSendingException ex, HttpServletRequest request) {

    ErrorResponse response =
        new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "OTP Email Sending Failed",
            ex.getMessage(),
            request.getRequestURI());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}

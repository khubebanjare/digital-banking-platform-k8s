package com.digitalpayment.auth.dto;

public record ResetPasswordRequest(String token, String newPassword, String confirmPassword) {}

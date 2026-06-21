package com.digitalpayment.auth.dto;

public record EnableMfaRequest(String email, String otp) {}

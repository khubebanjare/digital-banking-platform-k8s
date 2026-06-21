package com.digitalpayment.auth.dto;

public record DisableMfaRequest(String email, String otp) {}

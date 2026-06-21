package com.digitalpayment.auth.dto;

import java.time.Instant;

public record SessionResponse(Long id, String token, Instant expiryDate) {}

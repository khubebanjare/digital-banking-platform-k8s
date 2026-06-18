package com.digitalpayment.auth.dto;

import com.digitalpayment.auth.entity.Role;
import java.util.UUID;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn,
    UUID userId,
    String email,
    Role role) {}

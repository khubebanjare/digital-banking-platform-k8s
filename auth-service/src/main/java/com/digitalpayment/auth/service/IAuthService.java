package com.digitalpayment.auth.service;

import com.digitalpayment.auth.dto.AuthResponse;
import com.digitalpayment.auth.dto.LoginRequest;
import com.digitalpayment.auth.dto.RegisterRequest;

public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}

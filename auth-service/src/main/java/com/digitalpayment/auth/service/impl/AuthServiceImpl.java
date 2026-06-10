package com.digitalpayment.auth.service.impl;

import com.digitalpayment.auth.dto.AuthResponse;
import com.digitalpayment.auth.dto.LoginRequest;
import com.digitalpayment.auth.dto.RegisterRequest;
import com.digitalpayment.auth.entity.User;
import com.digitalpayment.auth.exception.AuthenticationException;
import com.digitalpayment.auth.exception.DuplicateEmailException;
import com.digitalpayment.auth.exception.InvalidCredentialsException;
import com.digitalpayment.auth.repository.UserRepository;
import com.digitalpayment.auth.service.IAuthService;
import com.digitalpayment.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration attempt with existing email: {}", request.getEmail());
            throw new DuplicateEmailException("Email already registered: " + request.getEmail());
        }
        
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());

        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), 
                user.getFirstName(), user.getLastName());
    }
    
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            log.info("Login attempt for email: {}", request.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            User user = (User) authentication.getPrincipal();
            if (user == null) {
                log.error("Authentication succeeded but principal is null for email: {}", request.getEmail());
                throw new InvalidCredentialsException("Invalid credentials");
            }
            
            String token = jwtUtil.generateToken(user.getEmail());
            log.info("User logged in successfully with email: {}", user.getEmail());
            return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(),
                    user.getFirstName(), user.getLastName());
                    
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            log.warn("Failed login attempt for email: {} - Invalid credentials", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            log.warn("Failed login attempt for email: {} - User not found", request.getEmail());
            throw new InvalidCredentialsException("User not found");
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login for email: {}", request.getEmail(), e);
            throw new AuthenticationException("Login failed due to server error", e);
        }
    }
}

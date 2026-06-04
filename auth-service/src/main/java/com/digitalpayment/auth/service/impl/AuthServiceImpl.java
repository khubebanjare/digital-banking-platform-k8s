package com.digitalpayment.auth.service.impl;

import com.digitalpayment.auth.dto.AuthResponse;
import com.digitalpayment.auth.dto.LoginRequest;
import com.digitalpayment.auth.dto.RegisterRequest;
import com.digitalpayment.auth.entity.User;
import com.digitalpayment.auth.exception.DuplicateEmailException;
import com.digitalpayment.auth.repository.UserRepository;
import com.digitalpayment.auth.service.IAuthService;
import com.digitalpayment.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already registered: " + request.getEmail());
        }
        
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        user = userRepository.save(user);
        System.out.println("Saved User ID: " + user.getId());

        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(), 
                user.getFirstName(), user.getLastName());
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            User user = (User) authentication.getPrincipal();
            if (user == null) {
                throw new IllegalArgumentException("Invalid credentials");
            }
            
            String token = jwtUtil.generateToken(user.getEmail());
            return new AuthResponse(token, "Bearer", user.getId(), user.getEmail(),
                    user.getFirstName(), user.getLastName());
                    
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new IllegalArgumentException("Invalid email or password");
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            throw new IllegalArgumentException("User not found");
        }
    }
}

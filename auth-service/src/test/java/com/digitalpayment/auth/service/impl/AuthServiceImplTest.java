package com.digitalpayment.auth.service.impl;

import com.digitalpayment.auth.dto.AuthResponse;
import com.digitalpayment.auth.dto.LoginRequest;
import com.digitalpayment.auth.dto.RegisterRequest;
import com.digitalpayment.auth.entity.Role;
import com.digitalpayment.auth.entity.User;
import com.digitalpayment.auth.exception.AuthenticationException;
import com.digitalpayment.auth.exception.DuplicateEmailException;
import com.digitalpayment.auth.exception.InvalidCredentialsException;
import com.digitalpayment.auth.repository.UserRepository;
import com.digitalpayment.auth.util.JwtUtil;
import com.digitalpayment.auth.metrics.AuthMetrics;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private AuthMetrics authMetrics;

    @Mock
    private Counter loginSuccessCounter;

    @Mock
    private Counter loginFailureCounter;

    @Mock
    private Counter registrationSuccessCounter;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        
        registerRequest = new RegisterRequest("John", "Doe", "john@example.com", "password123");
        loginRequest = new LoginRequest("john@example.com", "password123");
        
        user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setEnabled(true);

        when(authMetrics.getLoginSuccessCounter()).thenReturn(loginSuccessCounter);
        when(authMetrics.getLoginFailureCounter()).thenReturn(loginFailureCounter);
        when(authMetrics.getRegistrationSuccessCounter()).thenReturn(registrationSuccessCounter);
    }

    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken("john@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        
        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("password123");
        
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();
        assertEquals("John", capturedUser.getFirstName());
        assertEquals("Doe", capturedUser.getLastName());
        assertEquals("john@example.com", capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());
        
        verify(jwtUtil).generateToken("john@example.com");
        verify(registrationSuccessCounter).increment();
    }

    @Test
    void testRegisterDuplicateEmail() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> authService.register(registerRequest));
        
        verify(userRepository).existsByEmail("john@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken("john@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("john@example.com", response.getEmail());
        
        verify(authenticationManager).authenticate(any());
        verify(jwtUtil).generateToken("john@example.com");
        verify(loginSuccessCounter).increment();
        verify(loginFailureCounter, never()).increment();
    }

    @Test
    void testLoginBadCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(InvalidCredentialsException.class, () -> authService.login(loginRequest));
        
        verify(authenticationManager).authenticate(any());
        verify(loginFailureCounter).increment();
        verify(loginSuccessCounter, never()).increment();
    }

    @Test
    void testLoginUserNotFound() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));
        
        verify(authenticationManager).authenticate(any());
        verify(loginFailureCounter).increment();
        verify(loginSuccessCounter, never()).increment();
    }

    @Test
    void testLoginNullPrincipal() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(null, null);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));
        
        verify(authenticationManager).authenticate(any());
        verify(loginFailureCounter, atLeastOnce()).increment();
        verify(loginSuccessCounter, never()).increment();
    }

    @Test
    void testRegisterWithSystemOutVerification() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtUtil.generateToken("john@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(registrationSuccessCounter).increment();
    }

    @Test
    void testLoginUnexpectedException() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(AuthenticationException.class, () -> authService.login(loginRequest));
        
        verify(authenticationManager).authenticate(any());
        verify(loginFailureCounter).increment();
        verify(loginSuccessCounter, never()).increment();
    }
}

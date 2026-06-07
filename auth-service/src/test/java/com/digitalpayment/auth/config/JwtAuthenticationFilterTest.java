package com.digitalpayment.auth.config;

import com.digitalpayment.auth.entity.Role;
import com.digitalpayment.auth.entity.User;
import com.digitalpayment.auth.service.impl.CustomUserDetailsService;
import com.digitalpayment.auth.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private User user;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        user.setEnabled(true);
    }

    @Test
    void testDoFilterInternalWithValidToken() throws ServletException, IOException {
        String token = "valid-jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("john@example.com");
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(user);
        when(jwtUtil.validateToken(token, "john@example.com")).thenReturn(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("john@example.com");
        verify(jwtUtil).validateToken(token, "john@example.com");
        verify(filterChain).doFilter(request, response);
        
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(user, authentication.getPrincipal());
        assertNotNull(authentication.getDetails());
    }

    @Test
    void testDoFilterInternalWithNoAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void testDoFilterInternalWithInvalidBearerFormat() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic invalid");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
        String token = "invalid-jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenThrow(new IllegalArgumentException("Invalid token"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithUserNotFound() throws ServletException, IOException {
        String token = "valid-jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("notfound@example.com");
        when(userDetailsService.loadUserByUsername("notfound@example.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("notfound@example.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithInvalidatedToken() throws ServletException, IOException {
        String token = "expired-jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("john@example.com");
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(user);
        when(jwtUtil.validateToken(token, "john@example.com")).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("john@example.com");
        verify(jwtUtil).validateToken(token, "john@example.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithAlreadyAuthenticated() throws ServletException, IOException {
        String token = "valid-jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("john@example.com");
        
        org.springframework.security.core.Authentication auth = mock(org.springframework.security.core.Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(auth);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername(token);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
        
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternalWithNullValidationResult() throws ServletException, IOException {
        String token = "valid-jwt-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn("john@example.com");
        when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(user);
        when(jwtUtil.validateToken(token, "john@example.com")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername(token);
        verify(userDetailsService).loadUserByUsername("john@example.com");
        verify(jwtUtil).validateToken(token, "john@example.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void testDoFilterInternalWithEmptyAuthorizationHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(filterChain).doFilter(request, response);
        verify(jwtUtil, never()).extractUsername(anyString());
    }

    @Test
    void testDoFilterInternalWithBearerOnly() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request).getHeader("Authorization");
        verify(jwtUtil).extractUsername("");
        verify(filterChain).doFilter(request, response);
    }
}

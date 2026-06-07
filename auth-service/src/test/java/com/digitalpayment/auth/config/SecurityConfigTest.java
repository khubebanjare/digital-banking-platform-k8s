package com.digitalpayment.auth.config;

import com.digitalpayment.auth.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpSecurity httpSecurity;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private SecurityConfig securityConfig;

    @Test
    void testPasswordEncoderBeanCreation() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder);
        String encoded = passwordEncoder.encode("testPassword");
        assertNotNull(encoded);
    }

    @Test
    void testAuthenticationProviderBeanCreation() {
        AuthenticationProvider provider = securityConfig.authenticationProvider();

        assertNotNull(provider);
    }

    @Test
    void testPasswordEncoding() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "myPassword123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void testPasswordEncoderReturnsBCryptPasswordEncoder() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }

    @Test
    void testAuthenticationProviderUsesUserDetailsService() {
        AuthenticationProvider provider = securityConfig.authenticationProvider();

        assertNotNull(provider);
        assertInstanceOf(DaoAuthenticationProvider.class, provider);
    }

    @Test
    void testAuthenticationManagerBeanCreation() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager manager = securityConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(manager);
        verify(authenticationConfiguration).getAuthenticationManager();
    }

    @Test
    void testAuthenticationManagerReturnsCorrectManager() throws Exception {
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        AuthenticationManager manager = securityConfig.authenticationManager(authenticationConfiguration);

        assertNotNull(manager);
        assertSame(manager, authenticationManager);
    }

    @Test
    void testPasswordEncoderGeneratesDifferentHashesForSamePassword() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "myPassword123";
        String encodedPassword1 = encoder.encode(rawPassword);
        String encodedPassword2 = encoder.encode(rawPassword);

        assertNotNull(encodedPassword1);
        assertNotNull(encodedPassword2);
        assertTrue(encoder.matches(rawPassword, encodedPassword1));
        assertTrue(encoder.matches(rawPassword, encodedPassword2));
    }

    @Test
    void testPasswordEncoderDoesNotMatchWrongPassword() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "myPassword123";
        String encodedPassword = encoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertTrue(encoder.matches(rawPassword, encodedPassword));
        assertFalse(encoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void testSecurityFilterChainBeanCreation() throws Exception {
        when(httpSecurity.csrf(any())).thenReturn(httpSecurity);
        when(httpSecurity.authorizeHttpRequests(any())).thenReturn(httpSecurity);
        when(httpSecurity.sessionManagement(any())).thenReturn(httpSecurity);
        when(httpSecurity.authenticationProvider(any())).thenReturn(httpSecurity);
        when(httpSecurity.addFilterBefore(any(), any())).thenReturn(httpSecurity);
        
        org.springframework.security.web.DefaultSecurityFilterChain defaultChain = 
            mock(org.springframework.security.web.DefaultSecurityFilterChain.class);
        when(httpSecurity.build()).thenReturn(defaultChain);

        SecurityFilterChain chain = securityConfig.securityFilterChain(httpSecurity);

        assertNotNull(chain);
        verify(httpSecurity).csrf(any());
        verify(httpSecurity).authorizeHttpRequests(any());
        verify(httpSecurity).sessionManagement(any());
        verify(httpSecurity).authenticationProvider(any());
        verify(httpSecurity).addFilterBefore(eq(jwtAuthenticationFilter), any());
        verify(httpSecurity).build();
    }

    @Test
    void testAuthenticationProviderUsesPasswordEncoder() {
        AuthenticationProvider provider = securityConfig.authenticationProvider();
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        
        assertNotNull(provider);
        assertNotNull(encoder);
        
        String rawPassword = "testPassword";
        String encodedPassword = encoder.encode(rawPassword);
        
        assertTrue(encoder.matches(rawPassword, encodedPassword));
        
        UserDetails userDetails = User.withUsername("testuser")
                .password(encodedPassword)
                .roles("USER")
                .build();
        
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", rawPassword);
        
        Authentication result = provider.authenticate(authentication);
        
        assertNotNull(result);
        assertTrue(result.isAuthenticated());
    }
}

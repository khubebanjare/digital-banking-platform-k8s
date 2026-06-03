package com.digitalpayment.auth.config;

import com.digitalpayment.auth.service.impl.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private CustomUserDetailsService userDetailsService;

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

    private boolean assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but was false");
        }
        return true;
    }
}

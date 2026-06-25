package com.digitalpayment.auth.metrics;

import com.digitalpayment.auth.constants.AuthConstants;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AuthServiceMetrics {

  private final Counter loginSuccessCounter;
  private final Counter loginFailureCounter;
  private final Counter registrationSuccessCounter;
  private final Counter registrationFailureCounter;
  private final Counter logoutCounter;
  private final Counter refreshTokenCounter;
  private final Counter passwordResetCounter;
  private final Counter otpSentCounter;
  private final Counter otpVerifiedCounter;
  private final Counter mfaEnabledCounter;
  private final Counter mfaDisabledCounter;

  private final Timer loginTimer;
  private final Timer registrationTimer;
  private final Timer logoutTimer;
  private final Timer refreshTokenTimer;

  public AuthServiceMetrics(MeterRegistry registry) {
    // Counters
    this.loginSuccessCounter =
        Counter.builder("auth.login.success")
            .description("Successful login attempts")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.loginFailureCounter =
        Counter.builder("auth.login.failure")
            .description("Failed login attempts")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.registrationSuccessCounter =
        Counter.builder("auth.registration.success")
            .description("Successful user registrations")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.registrationFailureCounter =
        Counter.builder("auth.registration.failure")
            .description("Failed user registrations")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.logoutCounter =
        Counter.builder("auth.logout")
            .description("User logout operations")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.refreshTokenCounter =
        Counter.builder("auth.token.refresh")
            .description("Token refresh operations")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.passwordResetCounter =
        Counter.builder("auth.password.reset")
            .description("Password reset operations")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.otpSentCounter =
        Counter.builder("auth.otp.sent")
            .description("OTP sent operations")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.otpVerifiedCounter =
        Counter.builder("auth.otp.verified")
            .description("OTP verified operations")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.mfaEnabledCounter =
        Counter.builder("auth.mfa.enabled")
            .description("MFA enabled operations")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.mfaDisabledCounter =
        Counter.builder("auth.mfa.disabled")
            .description("MFA disabled operations")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    // Timers
    this.loginTimer =
        Timer.builder("auth.login.duration")
            .description("Login operation duration")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.registrationTimer =
        Timer.builder("auth.registration.duration")
            .description("Registration operation duration")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.logoutTimer =
        Timer.builder("auth.logout.duration")
            .description("Logout operation duration")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);

    this.refreshTokenTimer =
        Timer.builder("auth.token.refresh.duration")
            .description("Token refresh operation duration")
            .tag(AuthConstants.SERVICE, AuthConstants.SERVICE_NAME)
            .register(registry);
  }
}

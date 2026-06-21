//package com.digitalpayment.apigateway.metrics;
//
//import io.micrometer.core.instrument.Counter;
//import io.micrometer.core.instrument.MeterRegistry;
//import lombok.Getter;
//import org.springframework.stereotype.Component;
//
//@Getter
//@Component
//public class AuthMetrics {
//
//  private final Counter loginSuccessCounter;
//  private final Counter loginFailureCounter;
//  private final Counter registrationSuccessCounter;
//
//  public AuthMetrics(MeterRegistry registry) {
//
//    this.loginSuccessCounter =
//        Counter.builder("auth.login.success").description("Successful logins").register(registry);
//
//    this.loginFailureCounter =
//        Counter.builder("auth.login.failure").description("Failed logins").register(registry);
//
//    this.registrationSuccessCounter =
//        Counter.builder("auth.registration.success")
//            .description("Successful registrations")
//            .register(registry);
//  }
//}

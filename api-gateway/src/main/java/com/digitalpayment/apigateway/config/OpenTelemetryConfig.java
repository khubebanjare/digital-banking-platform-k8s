package com.digitalpayment.apigateway.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class OpenTelemetryConfig {

  @Value("${grafana.otlp.endpoint}")
  private String otlpEndpoint;

  @Value("${grafana.otlp.auth}")
  private String otlpAuth;


  @Value("${spring.application.name}")
  private String serviceName;

  @Bean
  public OpenTelemetry openTelemetry() {
    log.info("Configuring OpenTelemetry tracing for service: {}", serviceName);

    Resource resource =
            Resource.getDefault()
                    .toBuilder()
                    .put("service.name", serviceName)
                    .put("service.namespace", "digital-banking")
                    .put("deployment.environment", "k8s-local")
                    .build();

    SpanExporter spanExporter =
            OtlpGrpcSpanExporter.builder()
                    .setEndpoint(otlpEndpoint)
                    .addHeader("Authorization", "Bearer " + otlpAuth)
                    .build();

    SdkTracerProvider tracerProvider =
            SdkTracerProvider.builder()
                    .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build())
                    .setResource(resource)
                    .build();

    log.info("Creating OTLP HTTP log record exporter with endpoint: {}", otlpEndpoint);
    OtlpHttpLogRecordExporter logExporter =
            OtlpHttpLogRecordExporter.builder()
                    .setEndpoint(otlpEndpoint + "/v1/logs") // ← HTTP needs the /v1/logs path
                    .addHeader("Authorization", otlpAuth)
                    .build();

    SdkLoggerProvider loggerProvider =
            SdkLoggerProvider.builder()
                    .setResource(resource)
                    .addLogRecordProcessor(BatchLogRecordProcessor.builder(logExporter).build())
                    .build();

    OpenTelemetry openTelemetry =
            OpenTelemetrySdk.builder()
                    .setLoggerProvider(loggerProvider)
                    .setTracerProvider(tracerProvider)
                    .setPropagators(
                            ContextPropagators.create(JaegerPropagator.getInstance()))
                    .buildAndRegisterGlobal();

    log.info("OpenTelemetry tracing configured successfully");
    return openTelemetry;
  }

  @Bean
  public Tracer tracer(OpenTelemetry openTelemetry) {
    return openTelemetry.getTracer(serviceName, "1.0.0");
  }
}

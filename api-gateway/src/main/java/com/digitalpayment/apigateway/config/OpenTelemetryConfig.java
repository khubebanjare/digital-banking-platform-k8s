package com.digitalpayment.apigateway.config;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;
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

  @Bean
  public OpenTelemetrySdk openTelemetrySdk() {
    log.info("Configuring OpenTelemetry SDK for api-gateway");
    Resource resource =
        Resource.getDefault().toBuilder()
            .put(AttributeKey.stringKey("service.name"), "api-gateway")
            .put(AttributeKey.stringKey("service.namespace"), "digital-banking")
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

    OpenTelemetrySdk sdk =
        OpenTelemetrySdk.builder().setLoggerProvider(loggerProvider).buildAndRegisterGlobal();

    // Wire the logback appender to this SDK instance
    OpenTelemetryAppender.install(sdk);

    log.info("OpenTelemetry SDK configured successfully");
    return sdk;
  }
}

package com.digitalpayment.auth.config;

import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.exporter.otlp.http.logs.OtlpHttpLogRecordExporter;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.logs.SdkLoggerProvider;
import io.opentelemetry.sdk.logs.export.BatchLogRecordProcessor;
import io.opentelemetry.sdk.resources.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenTelemetryConfig {

    @Value("${grafana.otlp.endpoint}")
    private String otlpEndpoint;

    @Value("${grafana.otlp.auth}")
    private String otlpAuth;

    @Bean
    public OpenTelemetrySdk openTelemetrySdk() {
        Resource resource = Resource.getDefault().toBuilder()
                .put(AttributeKey.stringKey("service.name"), "auth-service")
                .put(AttributeKey.stringKey("service.namespace"), "digital-banking")
                .build();


        OtlpHttpLogRecordExporter logExporter = OtlpHttpLogRecordExporter.builder()
                .setEndpoint(otlpEndpoint + "/v1/logs")   // ← HTTP needs the /v1/logs path
                .addHeader("Authorization", otlpAuth)
                .build();

        SdkLoggerProvider loggerProvider = SdkLoggerProvider.builder()
                .setResource(resource)
                .addLogRecordProcessor(BatchLogRecordProcessor.builder(logExporter).build())
                .build();

        OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
                .setLoggerProvider(loggerProvider)
                .buildAndRegisterGlobal();

        // Wire the logback appender to this SDK instance
        OpenTelemetryAppender.install(sdk);

        return sdk;
    }

}

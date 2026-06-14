package com.digitalpayment.auth.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.boot.micrometer.metrics.autoconfigure.MeterRegistryCustomizer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ObservabilityConfigTest {

    @Test
    void testObservabilityConfigInitialization() {
        ObservabilityConfig config = new ObservabilityConfig();
        assertNotNull(config);
    }

    @Test
    void testCommonTagsBeanReturnsCustomizer() {
        ObservabilityConfig config = new ObservabilityConfig();
        MeterRegistryCustomizer<MeterRegistry> customizer = config.commonTags();
        assertNotNull(customizer);
    }

    @Test
    void testCommonTagsCustomizerAddsServiceTag() {
        ObservabilityConfig config = new ObservabilityConfig();
        MeterRegistryCustomizer<MeterRegistry> customizer = config.commonTags();
        
        MeterRegistry mockRegistry = mock(MeterRegistry.class);
        MeterRegistry.Config mockConfig = mock(MeterRegistry.Config.class);
        when(mockRegistry.config()).thenReturn(mockConfig);
        when(mockConfig.commonTags(anyString(), anyString(), anyString(), anyString())).thenReturn(mockConfig);
        
        customizer.customize(mockRegistry);
        
        verify(mockRegistry).config();
        verify(mockConfig).commonTags("service", "auth-service", "team", "banking");
    }

    @Test
    void testCommonTagsCustomizerAddsTeamTag() {
        ObservabilityConfig config = new ObservabilityConfig();
        MeterRegistryCustomizer<MeterRegistry> customizer = config.commonTags();
        
        MeterRegistry mockRegistry = mock(MeterRegistry.class);
        MeterRegistry.Config mockConfig = mock(MeterRegistry.Config.class);
        when(mockRegistry.config()).thenReturn(mockConfig);
        when(mockConfig.commonTags(anyString(), anyString(), anyString(), anyString())).thenReturn(mockConfig);
        
        customizer.customize(mockRegistry);
        
        verify(mockConfig).commonTags("service", "auth-service", "team", "banking");
    }
}

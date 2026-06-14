package com.digitalpayment.auth.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MeterConfigTest {

    @Test
    void testMeterConfigInstantiation() {
        MeterConfig meterConfig = new MeterConfig();
        assertNotNull(meterConfig);
    }

}

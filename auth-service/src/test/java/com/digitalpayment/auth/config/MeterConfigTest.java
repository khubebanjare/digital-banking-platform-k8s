package com.digitalpayment.auth.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MeterConfigTest {

  @Test
  void testMeterConfigInstantiation() {
    MeterConfig meterConfig = new MeterConfig();
    assertNotNull(meterConfig);
  }
}

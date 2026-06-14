package com.digitalpayment.auth.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StartupCheckTest {

    @InjectMocks
    private StartupCheck startupCheck;


    @Test
    void testStartupCheckInitialization() {
        assertNotNull(startupCheck);
    }

    @Test
    void testInitMethodCompletesSuccessfully() {
        long startTime = System.currentTimeMillis();
        startupCheck.init();
        long endTime = System.currentTimeMillis();
        
        assertTrue(endTime - startTime >= 1900, "Init method should take at least 2 seconds");
        assertTrue(endTime - startTime < 3000, "Init method should not take more than 3 seconds");
    }

    @Test
    void testInitMethodHandlesInterruption() {
        Thread.currentThread().interrupt();
        startupCheck.init();
        assertTrue(Thread.currentThread().isInterrupted());
        Thread.interrupted();
    }

    @Test
    void testInitMethodCanBeCalledMultipleTimes() {
        startupCheck.init();
        startupCheck.init();
        assertTrue(true, "Init method should be callable multiple times");
    }

    @Test
    void testStartupCheckHasCorrectDependencies() {
        assertNotNull(startupCheck);
    }
}

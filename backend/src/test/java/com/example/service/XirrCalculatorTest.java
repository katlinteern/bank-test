package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class XirrCalculatorTest {

    private XirrCalculator xirrCalculator;

    @BeforeEach
    public void setUp() {
        xirrCalculator = new XirrCalculator();
    }

    @Test
    public void testCalculateXirr_PositiveScenario() {
        // Test case where cash flows and dates are provided correctly
        List<BigDecimal> cashFlows = Arrays.asList(
            new BigDecimal("-10000"),  // Investment
            new BigDecimal("1000"),    // Cash inflow 1
            new BigDecimal("11000")   // Cash inflow 2
        );

        List<Instant> dates = Arrays.asList(
            Instant.parse("2022-01-01T00:00:00Z"),
            Instant.parse("2023-01-01T00:00:00Z"),
            Instant.parse("2024-01-01T00:00:00Z")
        );

        BigDecimal xirr = xirrCalculator.calculateXirr(dates, cashFlows);

        // Assert that the calculated XIRR is not null
        assertNotNull(xirr);

        // Assuming expected XIRR (this value should come from a known correct calculation)
        BigDecimal expectedXirr = new BigDecimal("0.10");  // 10% expected XIRR for test data
        assertEquals(expectedXirr.setScale(2, RoundingMode.HALF_UP), xirr.setScale(2, RoundingMode.HALF_UP));
    }

}

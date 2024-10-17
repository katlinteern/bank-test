package com.example.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static com.example.utils.XirrCalculator.calculateXirr;

class XirrCalculatorTest {

    // Using static methods to match the XirrCalculator class
    @Test
    void testCalculateXirrWithValidData() {
        List<Instant> dates = Arrays.asList(
                Instant.parse("2020-01-01T00:00:00Z"),
                Instant.parse("2021-01-01T00:00:00Z"),
                Instant.parse("2022-01-01T00:00:00Z")
        );
        List<BigDecimal> cashFlows = Arrays.asList(
                new BigDecimal(-1000), // investment
                new BigDecimal(500),   // cash flow
                new BigDecimal(600)    // cash flow
        );

        BigDecimal xirr = calculateXirr(dates, cashFlows);

        assertNotNull(xirr);
        assertTrue(xirr.compareTo(BigDecimal.ZERO) > 0, "XIRR should be positive");
    }
/* 
    @Test
    void testCalculateXirrWithEmptyLists() {
        List<Instant> dates = Collections.emptyList();
        List<BigDecimal> cashFlows = Collections.emptyList();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculateXirr(dates, cashFlows);
        });

        assertEquals("The number of dates and cash flows must be the same and cannot be null.", exception.getMessage());
    }

    @Test
    void testCalculateXirrWithMismatchedListSizes() {
        List<Instant> dates = Arrays.asList(
                Instant.parse("2020-01-01T00:00:00Z")
        );
        List<BigDecimal> cashFlows = Arrays.asList(
                new BigDecimal(-1000),
                new BigDecimal(500)
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculateXirr(dates, cashFlows);
        });

        assertEquals("The number of dates and cash flows must be the same and cannot be null.", exception.getMessage());
    }
*/
    // More test cases can be added here as needed
}

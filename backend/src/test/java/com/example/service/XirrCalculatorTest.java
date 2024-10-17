package com.example.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static com.example.utils.XirrCalculator.calculateXirr;
import com.example.dto.CashFlowData;

class XirrCalculatorTest {

    @Test
    public void testCalculateXirr_PositiveScenario() {
        List<CashFlowData> cashFlowDataList = Arrays.asList(
            new CashFlowData(new BigDecimal("-10000"), Instant.parse("2022-01-01T00:00:00Z")),  // Investment
            new CashFlowData(new BigDecimal("1000"), Instant.parse("2023-01-01T00:00:00Z")),    // Cash flow
            new CashFlowData(new BigDecimal("11000"), Instant.parse("2024-01-01T00:00:00Z"))   // Cash flow
        );

        BigDecimal xirr = calculateXirr(cashFlowDataList);

        System.out.println("Calculated XIRR: " + xirr);

        assertNotNull(xirr);

        BigDecimal expectedXirr = new BigDecimal("0.10");
        
        System.out.println("Expected XIRR: " + expectedXirr);

        assertEquals(expectedXirr.setScale(2, RoundingMode.HALF_UP), xirr.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    public void testCalculateXirr_WithMultipleCashFlowsIncludingZero() {
        List<CashFlowData> cashFlowDataList = Arrays.asList(
            new CashFlowData(new BigDecimal("-10000"), Instant.parse("2021-01-01T00:00:00Z")), // Investment
            new CashFlowData(new BigDecimal("0"), Instant.parse("2022-01-01T00:00:00Z")),       // Cash flow is zero
            new CashFlowData(new BigDecimal("2000"), Instant.parse("2023-01-01T00:00:00Z")),    // Cash flow is positive
            new CashFlowData(new BigDecimal("3000"), Instant.parse("2024-01-01T00:00:00Z"))     // Another positive cash flow
        );
    
        BigDecimal xirr = calculateXirr(cashFlowDataList);
    
        System.out.println("Calculated XIRR: " + xirr);
    
        assertNotNull(xirr);
        
        BigDecimal expectedXirr = new BigDecimal("-0.2316"); 

        assertEquals(expectedXirr.setScale(4, RoundingMode.HALF_UP), xirr.setScale(4, RoundingMode.HALF_UP));
    }
    

/*     @Test
    void testCalculateXirrWithEmptyLists() {
        List<CashFlowData> cashFlowDataList = Collections.emptyList();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculateXirr(cashFlowDataList);
        });

        assertEquals("The number of dates and cash flows must be the same and cannot be null.", exception.getMessage());
    }

    @Test
    void testCalculateXirrWithMismatchedListSizes() {
        List<CashFlowData> cashFlowDataList = Arrays.asList(
                new CashFlowData(new BigDecimal("-1000"), Instant.parse("2020-01-01T00:00:00Z")),
                new CashFlowData(new BigDecimal("500"), Instant.parse("2021-01-01T00:00:00Z"))
        );

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            calculateXirr(cashFlowDataList);
        });

        assertEquals("The number of dates and cash flows must be the same and cannot be null.", exception.getMessage());
    } */

    // More test cases can be added here as needed
}

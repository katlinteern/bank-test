package com.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class XirrCalculatorTest {

    private List<Instant> validDates;
    private List<BigDecimal> validCashFlows;

    @BeforeEach
    void setup() {
        // Standard dates and cashflows for normal XIRR calculation
        validDates = Arrays.asList(
                LocalDate.now().minusYears(2).atStartOfDay(ZoneOffset.UTC).toInstant(),
                LocalDate.now().minusYears(1).atStartOfDay(ZoneOffset.UTC).toInstant(),
                Instant.now());

        validCashFlows = Arrays.asList(
                BigDecimal.valueOf(-10000),
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(13000));
    }

    // Positive Result Tests
    @Test
    public void calculateXirr_withValidInputs_returnsExpectedXirr() {
        BigDecimal xirr = XirrCalculator.calculateXirr(validDates, validCashFlows);
        assertThat(xirr).isNotNull();
        assertThat(xirr.doubleValue()).isGreaterThan(0); // Expect a positive XIRR
    }

    @Test
    void testCalculateXirr_ValidInput() {
        List<Instant> dates = Arrays.asList(
                Instant.now(),
                Instant.now().plusSeconds(60),
                Instant.now().plusSeconds(120));
        List<BigDecimal> cashFlows = Arrays.asList(
                BigDecimal.valueOf(-1000),
                BigDecimal.valueOf(400),
                BigDecimal.valueOf(600));
        BigDecimal xirr = XirrCalculator.calculateXirr(dates, cashFlows);
        assertNotNull(xirr);
        assertTrue(xirr.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculateXirr_MaxIterationsReached() {
        List<Instant> dates = Arrays.asList(
                Instant.now(),
                Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(
                BigDecimal.valueOf(-1000),
                BigDecimal.valueOf(500));
        BigDecimal result = XirrCalculator.calculateXirr(dates, cashFlows);
        assertNotNull(result);
    }

    @Test
    void testCalculateXirr_LargeCashFlows() {
        List<Instant> dates = Arrays.asList(
                LocalDateTime.now().minusYears(10).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().toInstant(ZoneOffset.UTC));
        List<BigDecimal> cashFlows = Arrays.asList(
                BigDecimal.valueOf(-1_000_000),
                BigDecimal.valueOf(1_500_000));
        BigDecimal xirr = XirrCalculator.calculateXirr(dates, cashFlows);
        assertNotNull(xirr);
        assertTrue(xirr.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculateXirr_LongTimeInterval() {
        List<Instant> dates = Arrays.asList(
                LocalDateTime.now().minusYears(10).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().minusYears(5).toInstant(ZoneOffset.UTC),
                LocalDateTime.now().toInstant(ZoneOffset.UTC));
        List<BigDecimal> cashFlows = Arrays.asList(
                BigDecimal.valueOf(-1000),
                BigDecimal.valueOf(400),
                BigDecimal.valueOf(600));
        BigDecimal xirr = XirrCalculator.calculateXirr(dates, cashFlows);
        assertNotNull(xirr);
        assertTrue(xirr.compareTo(BigDecimal.ZERO) > 0);
    }

    /*     @Test
    void testCalculateXirr_HighlyDisparateCashFlows() {
        List<Instant> dates = Arrays.asList(
                Instant.now(),
                Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(
                BigDecimal.valueOf(-10), // small outflow
                BigDecimal.valueOf(1_000_000) // large inflow
        );
        BigDecimal xirr = XirrCalculator.calculateXirr(dates, cashFlows);
        assertNotNull(xirr);
        assertTrue(xirr.compareTo(BigDecimal.ZERO) >= 0, "XIRR should be greater than or equal to zero.");
    }

    @Test
    void testCalculateXirr_VeryCloseDates() {
        List<Instant> dates = Arrays.asList(
                Instant.now(),
                Instant.now().plusSeconds(1) // Extremely close time intervals
        );
        List<BigDecimal> cashFlows = Arrays.asList(
                BigDecimal.valueOf(-1000),
                BigDecimal.valueOf(1500) // Clear positive cash inflow
        );
        BigDecimal xirr = XirrCalculator.calculateXirr(dates, cashFlows);
        assertNotNull(xirr);
        assertTrue(xirr.compareTo(BigDecimal.ZERO) > 0, "XIRR should be greater than zero for positive cash inflow."); 
    } */
}

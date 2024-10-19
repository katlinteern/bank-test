package com.example.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    public void calculateXirr_withZeroCashFlow_returnsValidXirr() {
        List<BigDecimal> cashFlowsWithZero = Arrays.asList(
                BigDecimal.valueOf(-10000),
                BigDecimal.ZERO,
                BigDecimal.valueOf(13000));

        BigDecimal xirr = XirrCalculator.calculateXirr(validDates, cashFlowsWithZero);
        assertThat(xirr).isNotNull();
        assertThat(xirr.doubleValue()).isGreaterThan(0);
    }

    @Test
    public void calculateXirr_withFutureDates_returnsExpectedResult() {
        List<Instant> futureDates = Arrays.asList(
                Instant.now(),
                Instant.now().plusSeconds(86400), // Next day
                Instant.now().plusSeconds(172800) // Two days from no,
        );

        BigDecimal result = XirrCalculator.calculateXirr(futureDates, validCashFlows);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0, "XIRR should be a positive value with the given cash flows.");
    }

    // Cash Flow Test
    @Test
    public void calculateXirr_withAllNegativeCashFlows_returnsNull() {
        List<BigDecimal> negativeCashFlows = Arrays.asList(
                BigDecimal.valueOf(-10000),
                BigDecimal.valueOf(-2000),
                BigDecimal.valueOf(-3000));

        BigDecimal xirr = XirrCalculator.calculateXirr(validDates, negativeCashFlows);
        assertThat(xirr).isNull(); // Expect XIRR to be null with no positive cash flow
    }

    @Test
    public void calculateXirr_withAllPositiveCashFlows_returnsNull() {
        List<BigDecimal> positiveCashFlows = Arrays.asList(
                BigDecimal.valueOf(10000),
                BigDecimal.valueOf(2000),
                BigDecimal.valueOf(3000));

        BigDecimal xirr = XirrCalculator.calculateXirr(validDates, positiveCashFlows);
        assertThat(xirr).isNull(); // Expect XIRR to be null with no negative cash flow
    }

    // Input Validation Tests
    @Test
    public void calculateXirr_withNullDates_returnsNull() {
        BigDecimal xirr = XirrCalculator.calculateXirr(null, validCashFlows);
        assertThat(xirr).isNull(); // Invalid input should return null
    }

    @Test
    public void calculateXirr_withNullCashFlows_returnsNull() {
        BigDecimal xirr = XirrCalculator.calculateXirr(validDates, null);
        assertThat(xirr).isNull(); // Invalid input should return null
    }

    @Test
    public void calculateXirr_withMismatchedSizes_returnsNull() {
        List<BigDecimal> cashFlowsWithDifferentSize = Arrays.asList(
                BigDecimal.valueOf(-10000),
                BigDecimal.valueOf(2000));
        BigDecimal xirr = XirrCalculator.calculateXirr(validDates, cashFlowsWithDifferentSize);
        assertThat(xirr).isNull(); // Size mismatch should return null
    }

    @Test
    public void calculateXirr_withSingleCashFlow_returnsNull() {
        List<Instant> singleDate = Collections.singletonList(Instant.now());
        List<BigDecimal> singleCashFlow = Collections.singletonList(BigDecimal.valueOf(-10000));

        BigDecimal xirr = XirrCalculator.calculateXirr(singleDate, singleCashFlow);
        assertThat(xirr).isNull(); // XIRR calculation requires at least 2 cash flows
    }

    @Test
    public void calculateXirr_withAllZeroCashFlows_returnsNull() {
        List<BigDecimal> zeroCashFlows = Arrays.asList(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO);

        BigDecimal xirr = XirrCalculator.calculateXirr(validDates, zeroCashFlows);
        assertThat(xirr).isNull(); // If all cash flows are zero, return should be null
    }
/* 
    @Test
    public void calculateXirr_withSameDates_returnsNull() {
        Instant sameDate = Instant.now();
        List<Instant> sameDates = Arrays.asList(sameDate, sameDate, sameDate);
        List<BigDecimal> sameDateCashFlows = Arrays.asList(
                BigDecimal.valueOf(-10000),
                BigDecimal.valueOf(5000),
                BigDecimal.valueOf(7000));

        BigDecimal xirr = XirrCalculator.calculateXirr(sameDates, sameDateCashFlows);
        assertThat(xirr).isNull(); // Same date should lead to no valid XIRR calculation
    }
 */
    // Date Tests
    @Test
    public void calculateXirr_withDatesInWrongOrder_returnsNull() {
        List<Instant> dates = Arrays.asList(
                Instant.now().plusSeconds(86400), // Next day
                Instant.now() // Current time
        );
        List<BigDecimal> cashFlows = Arrays.asList(
                BigDecimal.valueOf(-1000),
                BigDecimal.valueOf(500));

        BigDecimal result = XirrCalculator.calculateXirr(dates, cashFlows);
        assertEquals(null, result, "XIRR should return null if dates are in the wrong order.");
    }

    @Test
    public void calculateXirr_withInsufficientData_returnsNull() {
        List<Instant> dates = Arrays.asList(Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(1000));

        BigDecimal result = XirrCalculator.calculateXirr(dates, cashFlows);
        assertEquals(null, result, "XIRR should return null with insufficient data.");
    }
}

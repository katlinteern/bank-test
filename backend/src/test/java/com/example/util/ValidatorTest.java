package com.example.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {

    @Test
    void testIsXirrInputValid_ValidInput() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-100), BigDecimal.valueOf(200));
        assertTrue(Validator.isXirrInputValid(dates, cashFlows));
    }
    @Test
    void testIsXirrInputValid_NullDates() {
        assertFalse(Validator.isXirrInputValid(null, Collections.singletonList(BigDecimal.TEN)));
    }

    @Test
    void testIsXirrInputValid_NullCashFlows() {
        assertFalse(Validator.isXirrInputValid(Collections.singletonList(Instant.now()), null));
    }

    @Test
    void testIsXirrInputValid_DifferentSizes() {
        List<Instant> dates = Collections.singletonList(Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.TEN, BigDecimal.ZERO);
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_InsufficientCashFlows() {
        List<Instant> dates = Collections.singletonList(Instant.now());
        List<BigDecimal> cashFlows = Collections.singletonList(BigDecimal.TEN);
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_EmptyLists() {
        assertFalse(Validator.isXirrInputValid(Collections.emptyList(), Collections.emptyList()));
    }

    @Test
    void testIsXirrInputValid_OneValidEntry() {
        List<Instant> dates = Arrays.asList(Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.TEN);
        assertFalse(Validator.isXirrInputValid(dates, cashFlows)); // Should be at least 2 cash flows
    }

    @Test
    void testIsXirrInputValid_AllZeroCashFlows() {
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO);
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_AllNegativeCashFlows() {
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-100), BigDecimal.valueOf(-200));
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_AllPositiveCashFlows() {
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(100), BigDecimal.valueOf(200));
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_DatesInChronologicalOrder() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-1000), BigDecimal.valueOf(100));
        assertTrue(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_DatesOutOfOrder() {
        List<Instant> dates = Arrays.asList(Instant.now().plusSeconds(60), Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-1000), BigDecimal.valueOf(100));
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_SameDates() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-1000), BigDecimal.valueOf(100));
        assertTrue(Validator.isXirrInputValid(dates, cashFlows)); // Same dates are allowed
    }

    @Test
    void testIsXirrInputValid_SingleDate() {
        List<Instant> dates = Collections.singletonList(Instant.now());
        List<BigDecimal> cashFlows = Collections.singletonList(BigDecimal.valueOf(-1000));
        assertFalse(Validator.isXirrInputValid(dates, cashFlows)); // Single date is not valid
    }

    @Test
    void testIsXirrInputValid_AllZeros() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO);
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_AllPositive() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.TEN, BigDecimal.valueOf(100));
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_AllNegative() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-10), BigDecimal.valueOf(-100));
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    // Tests for isRateValid method

    @Test
    public void testIsRateValid_ValidRate_ReturnsTrue() {
        double validRate = 0.5; // Valid rate greater than -1.0
        boolean result = Validator.isRateValid(validRate);
        assertTrue(result, "The rate should be valid.");
    }

    @Test
    public void testIsRateValid_RateEqualToNegativeOne_ReturnsFalse() {
        double invalidRate = -1.0; // Invalid rate
        boolean result = Validator.isRateValid(invalidRate);
        assertFalse(result, "The rate should be invalid as it is equal to -1.0.");
    }

    @Test
    public void testIsRateValid_RateLessThanNegativeOne_ReturnsFalse() {
        double invalidRate = -1.1; // Invalid rate
        boolean result = Validator.isRateValid(invalidRate);
        assertFalse(result, "The rate should be invalid as it is less than -1.0.");
    }

    @Test
    public void testIsRateValid_NegativeRate_ReturnsFalse() {
        double negativeRate = -0.5; // Valid rate greater than -1.0
        boolean result = Validator.isRateValid(negativeRate);
        assertTrue(result, "The rate should be valid as it is greater than -1.0.");
    }

    @Test
    public void testIsRateValid_ZeroRate_ReturnsTrue() {
        double zeroRate = 0.0; // Valid rate
        boolean result = Validator.isRateValid(zeroRate);
        assertTrue(result, "The rate should be valid as it is greater than -1.0.");
    }

    @Test
    public void testIsRateValid_PositiveRate_ReturnsTrue() {
        double positiveRate = 1.0; // Valid rate
        boolean result = Validator.isRateValid(positiveRate);
        assertTrue(result, "The rate should be valid.");
    }

    // Tests for isInvalidNpvOrDerivative method

    @Test
    void testIsInvalidNpvOrDerivative_NullNpv() {
        Double npv = null;
        Double npvDerivative = 0.01;
        assertTrue(Validator.isInvalidNpvOrDerivative(npv, npvDerivative), 
            "Should return true when NPV is null.");
    }

    @Test
    void testIsInvalidNpvOrDerivative_NullNpvDerivative() {
        Double npv = 0.01;
        Double npvDerivative = null;
        assertTrue(Validator.isInvalidNpvOrDerivative(npv, npvDerivative), 
            "Should return true when NPV derivative is null.");
    }

    @Test
    void testIsInvalidNpvOrDerivative_NpvIsNaN() {
        Double npv = Double.NaN;
        Double npvDerivative = 0.01;
        assertTrue(Validator.isInvalidNpvOrDerivative(npv, npvDerivative), 
            "Should return true when NPV is NaN.");
    }

    @Test
    void testIsInvalidNpvOrDerivative_NpvDerivativeIsNaN() {
        Double npv = 0.01;
        Double npvDerivative = Double.NaN;
        assertTrue(Validator.isInvalidNpvOrDerivative(npv, npvDerivative), 
            "Should return true when NPV derivative is NaN.");
    }

    @Test
    void testIsInvalidNpvOrDerivative_NpvIsInfinite() {
        Double npv = Double.POSITIVE_INFINITY;
        Double npvDerivative = 0.01;
        assertTrue(Validator.isInvalidNpvOrDerivative(npv, npvDerivative), 
            "Should return true when NPV is infinite.");
    }

    @Test
    void testIsInvalidNpvOrDerivative_NpvDerivativeIsInfinite() {
        Double npv = 0.01;
        Double npvDerivative = Double.POSITIVE_INFINITY;
        assertTrue(Validator.isInvalidNpvOrDerivative(npv, npvDerivative), 
            "Should return true when NPV derivative is infinite.");
    }

    @Test
    void testIsInvalidNpvOrDerivative_ValidValues() {
        Double npv = 0.01;
        Double npvDerivative = 0.005;
        assertFalse(Validator.isInvalidNpvOrDerivative(npv, npvDerivative), 
            "Should return false when both NPV and NPV derivative are valid.");
    }

    // Tests for isRateConverged method

    @Test
    void testIsRateConverged_ExactMatch() {
        double rate = 0.05;
        double newRate = 0.05;
        double precision = 1e-6;
        assertTrue(Validator.isRateConverged(rate, newRate, precision), 
            "Should return true when rate and newRate are exactly the same.");
    }

    @Test
    void testIsRateConverged_WithinPrecision() {
        double rate = 0.05;
        double newRate = 0.0500001;
        double precision = 1e-4;
        assertTrue(Validator.isRateConverged(rate, newRate, precision), 
            "Should return true when the difference is within the precision.");
    }

    @Test
    void testIsRateConverged_OutsidePrecision() {
        double rate = 0.05;
        double newRate = 0.051;
        double precision = 1e-4;
        assertFalse(Validator.isRateConverged(rate, newRate, precision), 
            "Should return false when the difference is outside the precision.");
    }

    @Test
    void testIsRateConverged_ZeroPrecision() {
        double rate = 0.05;
        double newRate = 0.06;
        double precision = 0.0;
        assertFalse(Validator.isRateConverged(rate, newRate, precision), 
            "Should return false when precision is 0 and rates are different.");
    }

    @Test
    void testIsRateConverged_NegativePrecision() {
        double rate = 0.05;
        double newRate = 0.06;
        double precision = -1e-4;
        assertFalse(Validator.isRateConverged(rate, newRate, precision), 
            "Should return false when precision is negative.");
    }

    @Test
    void testIsRateConverged_NegativeRates() {
        double rate = -0.05;
        double newRate = -0.0499999;
        double precision = 1e-6;
        assertTrue(Validator.isRateConverged(rate, newRate, precision), 
            "Should return true when negative rates are within precision.");
    }

    @Test
    void testIsRateConverged_SmallDifference() {
        double rate = 0.05;
        double newRate = 0.0500000001;
        double precision = 1e-9;
        assertTrue(Validator.isRateConverged(rate, newRate, precision), 
            "Should return true when the difference is extremely small and within precision.");
    }

    @Test
    void testIsRateConverged_LargeDifference() {
        double rate = 0.05;
        double newRate = 0.10;
        double precision = 1e-6;
        assertFalse(Validator.isRateConverged(rate, newRate, precision), 
            "Should return false when the difference between rates is large.");
    }
}
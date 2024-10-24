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
    void testIsXirrInputValid_DatesInChronologicalOrder() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-1000), BigDecimal.valueOf(100));
        assertTrue(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_SameDates() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-1000), BigDecimal.valueOf(100));
        assertTrue(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_VeryLargeCashFlows() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-Double.MAX_VALUE), BigDecimal.valueOf(100));
        assertTrue(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_VerySmallCashFlows() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-0.0000001), BigDecimal.valueOf(0.0000002));
        assertTrue(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_SameDateWithDifferentCashFlows() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-100), BigDecimal.valueOf(100));
        assertTrue(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_YearApartDates() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(31536000));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-1000), BigDecimal.valueOf(2000));
        assertTrue(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_EmptyLists() {
        assertFalse(Validator.isXirrInputValid(Collections.emptyList(), Collections.emptyList()));
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
    void testIsXirrInputValid_OneValidEntry() {
        List<Instant> dates = Arrays.asList(Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.TEN);
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
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
    void testIsXirrInputValid_DatesOutOfOrder() {
        List<Instant> dates = Arrays.asList(Instant.now().plusSeconds(60), Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-1000), BigDecimal.valueOf(100));
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_SingleDate() {
        List<Instant> dates = Collections.singletonList(Instant.now());
        List<BigDecimal> cashFlows = Collections.singletonList(BigDecimal.valueOf(-1000));
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsXirrInputValid_AllZeros() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO);
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }
}

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
    void testHasValidSize_ValidInput() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.TEN, BigDecimal.ZERO);
        assertTrue(Validator.hasValidSize(dates, cashFlows));
    }

    @Test
    void testHasValidSize_NullDates() {
        assertFalse(Validator.hasValidSize(null, Collections.singletonList(BigDecimal.TEN)));
    }

    @Test
    void testHasValidSize_NullCashFlows() {
        assertFalse(Validator.hasValidSize(Collections.singletonList(Instant.now()), null));
    }

    @Test
    void testHasValidSize_DifferentSizes() {
        List<Instant> dates = Collections.singletonList(Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.TEN, BigDecimal.ZERO);
        assertFalse(Validator.hasValidSize(dates, cashFlows));
    }

    @Test
    void testHasValidSize_InsufficientCashFlows() {
        List<Instant> dates = Collections.singletonList(Instant.now());
        List<BigDecimal> cashFlows = Collections.singletonList(BigDecimal.TEN);
        assertFalse(Validator.hasValidSize(dates, cashFlows));
    }

    @Test
    void testAreCashFlowsAllZero() {
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO);
        assertTrue(Validator.areCashFlowsAllZero(cashFlows));
    }

    @Test
    void testAreCashFlowsAllNegative() {
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(-100), BigDecimal.valueOf(-200));
        assertTrue(Validator.areCashFlowsAllNegative(cashFlows));
    }

    @Test
    void testAreCashFlowsAllPositive() {
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(100), BigDecimal.valueOf(200));
        assertTrue(Validator.areCashFlowsAllPositive(cashFlows));
    }

    @Test
    void testAreDatesInChronologicalOrder_ValidOrder() {
        List<Instant> dates = Arrays.asList(Instant.now(), Instant.now().plusSeconds(60));
        assertTrue(Validator.areDatesInChronologicalOrder(dates));
    }

    @Test
    void testAreDatesInChronologicalOrder_InvalidOrder() {
        List<Instant> dates = Arrays.asList(Instant.now().plusSeconds(60), Instant.now());
        assertFalse(Validator.areDatesInChronologicalOrder(dates));
    }

    @Test
    void testIsInputValid_ValidInput() {
        List<Instant> dates = Arrays.asList(
                Instant.now(),
                Instant.now().plusSeconds(60),
                Instant.now().plusSeconds(120));
        List<BigDecimal> cashFlows = Arrays.asList(
                BigDecimal.valueOf(-1000),
                BigDecimal.valueOf(400),
                BigDecimal.valueOf(600));
        assertTrue(Validator.isXirrInputValid(dates, cashFlows));
    }

    @Test
    void testIsInputValid_InvalidInput() {
        List<Instant> dates = Arrays.asList(Instant.now().plusSeconds(60), Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(BigDecimal.valueOf(100), BigDecimal.valueOf(-50));
        assertFalse(Validator.isXirrInputValid(dates, cashFlows));
    }
}

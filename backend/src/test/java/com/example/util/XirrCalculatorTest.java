package com.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XirrCalculatorTest {

    @InjectMocks
    private XirrCalculator xirrCalculator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculateXirr_ValidInput() {
        Instant startDate = Instant.parse("2022-01-01T00:00:00Z");
        List<Instant> dates = Arrays.asList(
                startDate,
                startDate.plus(365, ChronoUnit.DAYS), // 2023-01-01
                startDate.plus(730, ChronoUnit.DAYS) // 2024-01-01
        );
        List<BigDecimal> cashFlows = Arrays.asList(
                new BigDecimal(-10000), // 2022-01-01
                new BigDecimal(2000), // 2023-01-01
                new BigDecimal(9000) // 2024-01-01
        );

        BigDecimal expectedXirr = new BigDecimal("0.05393920142");
        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertNotNull(result);
        assertEquals(expectedXirr.setScale(10, RoundingMode.HALF_UP),
                result.setScale(10, RoundingMode.HALF_UP));
    }

    @Test
    void testCalculateXirr_ValidInputWithZeroCashFlow() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now.minusSeconds(86400), now, now.plusSeconds(86400));
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(-1000), new BigDecimal(0), new BigDecimal(1100));

        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculateXirr_ValidNegativeCashFlow() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now, now.plusSeconds(86400));
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(-1000), new BigDecimal(100));

        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertTrue(result.compareTo(BigDecimal.ZERO) < 0, "XIRR should be negative due to net loss");
    }

    @Test
    void testCalculateXirr_LargeCashFlows() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now.minus(30, ChronoUnit.DAYS), now.plus(365, ChronoUnit.DAYS));
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal("-1000000000"), new BigDecimal("1500000000"));

        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculateXirr_SmallTimeDifferences() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now, now.plus(1, ChronoUnit.DAYS));
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal("-1000"), new BigDecimal("1001"));

        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculateXirr_LongPeriodBetweenCashFlows() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now.minus(3650, ChronoUnit.DAYS), now);
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal("-1000"), new BigDecimal("15000"));

        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculateXirr_RepeatedDates() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now, now, now.plus(365, ChronoUnit.DAYS));
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal("-1000"), new BigDecimal("-500"),
                new BigDecimal("1600"));

        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculateXirr_SingleInflowOutflow() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now.minus(365, ChronoUnit.DAYS), now);
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal("-1000"), new BigDecimal("1200"));

        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculateXirr_SkewedCashFlows() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now.minus(365, ChronoUnit.DAYS), now.plus(30, ChronoUnit.DAYS),
                now.plus(365, ChronoUnit.DAYS));
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal("-10000"), new BigDecimal("100"),
                new BigDecimal("10500"));

        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculateXirr_NegativeAndPositiveCashFlows() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now, now.plus(365, ChronoUnit.DAYS), now.plus(730, ChronoUnit.DAYS));
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(-1000), new BigDecimal(500), new BigDecimal(600));

        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0, "XIRR should be positive due to net positive cash flow");
    }

    @Test
    void testCalculateXirr_ThreeCashFlowsOneDayApart() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now, now.plus(1, ChronoUnit.DAYS), now.plus(2, ChronoUnit.DAYS));
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(-1000), new BigDecimal(500), new BigDecimal(600));

        BigDecimal result = xirrCalculator.calculateXirr(dates, cashFlows);

        assertNotNull(result);
        assertTrue(result.compareTo(BigDecimal.ZERO) > 0, "XIRR should be positive due to net cash flow");
    }

    @Test
    void testCalculateXirr_ZeroCashFlows() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now.minusSeconds(86400), now);
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(0), new BigDecimal(0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xirrCalculator.calculateXirr(dates, cashFlows);
        });

        assertEquals("Invalid input: dates or cash flows are invalid.", exception.getMessage());
    }

    @Test
    void testCalculateXirr_AllPositiveCashFlows() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now, now.plus(365, ChronoUnit.DAYS));
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal("1000"), new BigDecimal("2000"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xirrCalculator.calculateXirr(dates, cashFlows);
        });

        assertEquals("Invalid input: dates or cash flows are invalid.", exception.getMessage());
    }

    @Test
    void testCalculateXirr_SingleCashFlow() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now);
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(1000));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xirrCalculator.calculateXirr(dates, cashFlows);
        });

        assertEquals("Invalid input: dates or cash flows are invalid.", exception.getMessage());
    }

    @Test
    void testCalculateXirr_NegativeCashFlows() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now.minusSeconds(86400), now);
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(-1000), new BigDecimal(-100));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xirrCalculator.calculateXirr(dates, cashFlows);
        });

        assertEquals("Invalid input: dates or cash flows are invalid.", exception.getMessage());
    }

    @Test
    void testCalculateXirr_EmptyDates() {
        List<Instant> dates = Collections.emptyList();
        List<BigDecimal> cashFlows = Collections.emptyList();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xirrCalculator.calculateXirr(dates, cashFlows);
        });

        assertEquals("Invalid input: dates or cash flows are invalid.", exception.getMessage());
    }

    @Test
    void testCalculateXirr_InsufficientCashFlows() {
        List<Instant> dates = Arrays.asList(Instant.now());
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(-1000));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xirrCalculator.calculateXirr(dates, cashFlows);
        });

        assertEquals("Invalid input: dates or cash flows are invalid.", exception.getMessage());
    }

    @Test
    void testCalculateXirr_InvalidCashFlows() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now.minusSeconds(86400), now);
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(-1000), new BigDecimal(0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xirrCalculator.calculateXirr(dates, cashFlows);
        });

        assertEquals("Invalid input: dates or cash flows are invalid.", exception.getMessage());
    }

    @Test
    void testCalculateXirr_MultipleZeroCashFlows() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now, now.plus(30, ChronoUnit.DAYS), now.plus(60, ChronoUnit.DAYS));
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(0), new BigDecimal(0), new BigDecimal(0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xirrCalculator.calculateXirr(dates, cashFlows);
        });

        assertEquals("Invalid input: dates or cash flows are invalid.", exception.getMessage());
    }

    @Test
    void testCalculateXirr_NegativeAndZeroCashFlows() {
        Instant now = Instant.now();
        List<Instant> dates = Arrays.asList(now.minusSeconds(86400), now);
        List<BigDecimal> cashFlows = Arrays.asList(new BigDecimal(-1000), new BigDecimal(0));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            xirrCalculator.calculateXirr(dates, cashFlows);
        });

        assertEquals("Invalid input: dates or cash flows are invalid.", exception.getMessage());
    }
}

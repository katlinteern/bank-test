package com.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class DividendTest {

    private Dividend dividend;

    @BeforeEach
    public void setUp() {
        dividend = new Dividend();
        dividend.setAmount(BigDecimal.valueOf(100));
        dividend.setTimestamp(Instant.now());
    }

    @Test
    public void testGetId() {
        dividend.setId(1L);
        assertEquals(1L, dividend.getId());
    }

    @Test
    public void testGetAmount() {
        assertEquals(BigDecimal.valueOf(100), dividend.getAmount());
    }

    @Test
    public void testSetAmount_ValidValue() {
        dividend.setAmount(BigDecimal.valueOf(150));
        assertEquals(BigDecimal.valueOf(150), dividend.getAmount());
    }

    @Test
    public void testSetAmount_NegativeValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dividend.setAmount(BigDecimal.valueOf(-50));
        });
        assertEquals("Amount cannot be negative or null", exception.getMessage());
    }

    @Test
    public void testSetAmount_NullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            dividend.setAmount(null);
        });
        assertEquals("Amount cannot be negative or null", exception.getMessage());
    }

    @Test
    public void testGetTimestamp() {
        Instant now = Instant.now();
        dividend.setTimestamp(now);
        assertEquals(now, dividend.getTimestamp());
    }

    @Test
    public void testSetTimestamp() {
        Instant future = Instant.now().plusSeconds(3600);
        dividend.setTimestamp(future);
        assertEquals(future, dividend.getTimestamp());
    }

    @Test
    public void testGetInvestment() {
        Investment investment = new Investment();
        dividend.setInvestment(investment);
        assertEquals(investment, dividend.getInvestment());
    }

    @Test
    public void testSetInvestment() {
        Investment investment = new Investment();
        dividend.setInvestment(investment);
        assertEquals(investment, dividend.getInvestment());
    }
}

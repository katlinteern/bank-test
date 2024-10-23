package com.example.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class InvestmentTest {

    private Investment investment;

    @BeforeEach
    public void setUp() {
        investment = new Investment();
        investment.setUserId(1L);
        investment.setName("Test Investment");
        investment.setCurrentPrice(BigDecimal.valueOf(100));
        investment.setTransactions(Collections.emptyList());
        investment.setDividends(Collections.emptyList());
    }

    @Test
    public void testGetId() {
        investment.setId(1L);
        assertEquals(1L, investment.getId());
    }

    @Test
    public void testGetUserId() {
        assertEquals(1L, investment.getUserId());
    }

    @Test
    public void testSetUserId() {
        investment.setUserId(2L);
        assertEquals(2L, investment.getUserId());
    }

    @Test
    public void testGetName() {
        assertEquals("Test Investment", investment.getName());
    }

    @Test
    public void testSetName() {
        investment.setName("Updated Investment");
        assertEquals("Updated Investment", investment.getName());
    }

    @Test
    public void testGetCurrentPrice() {
        assertEquals(BigDecimal.valueOf(100), investment.getCurrentPrice());
    }

    @Test
    public void testSetCurrentPrice_ValidValue() {
        investment.setCurrentPrice(BigDecimal.valueOf(150));
        assertEquals(BigDecimal.valueOf(150), investment.getCurrentPrice());
    }

    @Test
    public void testSetCurrentPrice_NegativeValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            investment.setCurrentPrice(BigDecimal.valueOf(-50));
        });
        assertEquals("Price cannot be negative or null", exception.getMessage());
    }

    @Test
    public void testSetCurrentPrice_NullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            investment.setCurrentPrice(null);
        });
        assertEquals("Price cannot be negative or null", exception.getMessage());
    }

    @Test
    public void testGetTransactions() {
        assertEquals(Collections.emptyList(), investment.getTransactions());
    }

    @Test
    public void testSetTransactions() {
        Transaction transaction = new Transaction();
        investment.setTransactions(Collections.singletonList(transaction));
        assertEquals(1, investment.getTransactions().size());
    }

    @Test
    public void testGetDividends() {
        assertEquals(Collections.emptyList(), investment.getDividends());
    }

    @Test
    public void testSetDividends() {
        Dividend dividend = new Dividend();
        investment.setDividends(Collections.singletonList(dividend));
        assertEquals(1, investment.getDividends().size());
    }
}

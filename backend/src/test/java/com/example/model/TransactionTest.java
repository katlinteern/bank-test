package com.example.model;

import com.example.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(10);
        transaction.setPrice(BigDecimal.valueOf(100));
        transaction.setTimestamp(Instant.now());
    }

    @Test
    public void testGetId() {
        transaction.setId(1L);
        assertEquals(1L, transaction.getId());
    }

    @Test
    public void testGetType() {
        assertEquals(TransactionType.BUY, transaction.getType());
    }

    @Test
    public void testSetType() {
        transaction.setType(TransactionType.SELL);
        assertEquals(TransactionType.SELL, transaction.getType());
    }

    @Test
    public void testGetQuantity() {
        assertEquals(10, transaction.getQuantity());
    }

    @Test
    public void testSetQuantity_ValidValue() {
        transaction.setQuantity(5);
        assertEquals(5, transaction.getQuantity());
    }

    @Test
    public void testSetQuantity_NegativeValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transaction.setQuantity(-1);
        });
        assertEquals("Quantity cannot be negative", exception.getMessage());
    }

    @Test
    public void testGetPrice() {
        assertEquals(BigDecimal.valueOf(100), transaction.getPrice());
    }

    @Test
    public void testSetPrice_ValidValue() {
        transaction.setPrice(BigDecimal.valueOf(150));
        assertEquals(BigDecimal.valueOf(150), transaction.getPrice());
    }

    @Test
    public void testSetPrice_NegativeValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transaction.setPrice(BigDecimal.valueOf(-50));
        });
        assertEquals("Price cannot be negative or null", exception.getMessage());
    }

    @Test
    public void testSetPrice_NullValue() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transaction.setPrice(null);
        });
        assertEquals("Price cannot be negative or null", exception.getMessage());
    }

    @Test
    public void testGetFee() {
        transaction.setFee(BigDecimal.valueOf(5));
        assertEquals(BigDecimal.valueOf(5), transaction.getFee());
    }

    @Test
    public void testSetFee() {
        transaction.setFee(BigDecimal.valueOf(10));
        assertEquals(BigDecimal.valueOf(10), transaction.getFee());
    }

    @Test
    public void testGetTimestamp() {
        Instant now = Instant.now();
        transaction.setTimestamp(now);
        assertEquals(now, transaction.getTimestamp());
    }

    @Test
    public void testSetTimestamp() {
        Instant future = Instant.now().plusSeconds(3600);
        transaction.setTimestamp(future);
        assertEquals(future, transaction.getTimestamp());
    }

    @Test
    public void testGetInvestment() {
        Investment investment = new Investment();
        transaction.setInvestment(investment);
        assertEquals(investment, transaction.getInvestment());
    }

    @Test
    public void testSetInvestment() {
        Investment investment = new Investment();
        transaction.setInvestment(investment);
        assertEquals(investment, transaction.getInvestment());
    }
}

package com.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.enums.TransactionType;
import com.example.model.Transaction;

class TransactionServiceTest {

    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        transactionService = new TransactionService();
    }

    @Test
    public void calculateCashFlow_BuyTransaction_ReturnsNegativeCashFlow() {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setPrice(BigDecimal.valueOf(10));
        transaction.setQuantity(5);
        transaction.setFee(BigDecimal.valueOf(1));

        BigDecimal cashFlow = transactionService.calculateCashFlow(transaction);

        assertEquals(BigDecimal.valueOf(-51), cashFlow); // (-10 * 5) - 1 = -51
    }

    @Test
    public void calculateCashFlow_SellTransaction_ReturnsPositiveCashFlow() {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.SELL);
        transaction.setPrice(BigDecimal.valueOf(15));
        transaction.setQuantity(3);
        transaction.setFee(BigDecimal.valueOf(2));

        BigDecimal cashFlow = transactionService.calculateCashFlow(transaction);

        assertEquals(BigDecimal.valueOf(43), cashFlow); // (15 * 3) - 2 = 43
    }

    @Test
    public void calculateCashFlow_BuyTransaction_WithZeroFee_ReturnsNegativeCashFlow() {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setPrice(BigDecimal.valueOf(20));
        transaction.setQuantity(4);
        transaction.setFee(BigDecimal.ZERO);

        BigDecimal cashFlow = transactionService.calculateCashFlow(transaction);

        assertEquals(BigDecimal.valueOf(-80), cashFlow); // (-20 * 4) - 0 = -80
    }

    @Test
    public void calculateCashFlow_SellTransaction_WithZeroFee_ReturnsPositiveCashFlow() {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.SELL);
        transaction.setPrice(BigDecimal.valueOf(25));
        transaction.setQuantity(2);
        transaction.setFee(BigDecimal.ZERO);

        BigDecimal cashFlow = transactionService.calculateCashFlow(transaction);

        assertEquals(BigDecimal.valueOf(50), cashFlow); // (25 * 2) - 0 = 50
    }
}

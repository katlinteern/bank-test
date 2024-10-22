package com.example.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import com.example.enums.TransactionType;
import com.example.model.Transaction;

class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void calculateCashFlow_BuyTransaction_ReturnsNegativeCashFlow() {
        Transaction transaction = createTransaction(TransactionType.BUY, BigDecimal.valueOf(10), 5, BigDecimal.valueOf(1));
        BigDecimal cashFlow = transactionService.calculateCashFlow(transaction);
        assertEquals(BigDecimal.valueOf(-51), cashFlow);
    }

    @Test
    public void calculateCashFlow_SellTransaction_ReturnsPositiveCashFlow() {
        Transaction transaction = createTransaction(TransactionType.SELL, BigDecimal.valueOf(15), 3, BigDecimal.valueOf(2));
        BigDecimal cashFlow = transactionService.calculateCashFlow(transaction);
        assertEquals(BigDecimal.valueOf(43), cashFlow);
    }

    @Test
    public void calculateCashFlow_BuyTransaction_WithZeroFee_ReturnsNegativeCashFlow() {
        Transaction transaction = createTransaction(TransactionType.BUY, BigDecimal.valueOf(20), 4, BigDecimal.ZERO);
        BigDecimal cashFlow = transactionService.calculateCashFlow(transaction);
        assertEquals(BigDecimal.valueOf(-80), cashFlow);
    }

    @Test
    public void calculateCashFlow_SellTransaction_WithZeroFee_ReturnsPositiveCashFlow() {
        Transaction transaction = createTransaction(TransactionType.SELL, BigDecimal.valueOf(25), 2, BigDecimal.ZERO);
        BigDecimal cashFlow = transactionService.calculateCashFlow(transaction);
        assertEquals(BigDecimal.valueOf(50), cashFlow);
    }

    @Test
    public void calculateCashFlow_NullTransaction_ReturnsZero() {
        BigDecimal cashFlow = transactionService.calculateCashFlow(null);
        assertEquals(BigDecimal.ZERO, cashFlow);
    }

    @Test
    public void calculateTotalQuantity_EmptyTransactionList_ReturnsZero() {
        List<Transaction> transactions = Collections.emptyList();
        int totalQuantity = transactionService.calculateTotalQuantity(transactions);
        assertEquals(0, totalQuantity);
    }

    @Test
    public void calculateTotalQuantity_SingleBuyTransaction_ReturnsPositiveQuantity() {
        Transaction transaction = createTransaction(TransactionType.BUY, BigDecimal.valueOf(10), 5, BigDecimal.ZERO);
        List<Transaction> transactions = Collections.singletonList(transaction);
        int totalQuantity = transactionService.calculateTotalQuantity(transactions);
        assertEquals(5, totalQuantity);
    }

    @Test
    public void calculateTotalQuantity_SingleSellTransaction_ReturnsNegativeQuantity() {
        Transaction transaction = createTransaction(TransactionType.SELL, BigDecimal.valueOf(10), 3, BigDecimal.ZERO);
        List<Transaction> transactions = Collections.singletonList(transaction);
        int totalQuantity = transactionService.calculateTotalQuantity(transactions);
        assertEquals(-3, totalQuantity);
    }

    @Test
    public void calculateTotalQuantity_BuyAndSellTransactions_ReturnsCorrectQuantity() {
        Transaction buyTransaction = createTransaction(TransactionType.BUY, BigDecimal.valueOf(10), 5, BigDecimal.ZERO);
        Transaction sellTransaction = createTransaction(TransactionType.SELL, BigDecimal.valueOf(10), 3, BigDecimal.ZERO);
        List<Transaction> transactions = List.of(buyTransaction, sellTransaction);
        int totalQuantity = transactionService.calculateTotalQuantity(transactions);
        assertEquals(2, totalQuantity); // 5 - 3 = 2
    }

    @Test
    public void calculateTotalQuantity_NegativeQuantities_ReturnsCorrectQuantity() {
        Transaction buyTransaction = createTransaction(TransactionType.BUY, BigDecimal.valueOf(10), -5, BigDecimal.ZERO);
        List<Transaction> transactions = Collections.singletonList(buyTransaction);
        int totalQuantity = transactionService.calculateTotalQuantity(transactions);
        assertEquals(-5, totalQuantity);
    }

    private Transaction createTransaction(TransactionType type, BigDecimal price, int quantity, BigDecimal fee) {
        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setPrice(price);
        transaction.setQuantity(quantity);
        transaction.setFee(fee);
        return transaction;
    }
}

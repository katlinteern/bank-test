package com.example.generator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.enums.TransactionType;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.repository.TransactionRepository;

public class DataGeneratorTest {

    @InjectMocks
    private DataGenerator dataGenerator;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSellQuantitiesDoNotExceedBuyQuantities() {
        // Generate an investment with transactions
        Investment investment = new Investment();
        investment.setName("Test Investment");
        investment.setCurrentPrice(BigDecimal.valueOf(100));
        investment.setUserId(1L);

        List<Transaction> transactions = dataGenerator.generateTransactions(investment);

        int totalBuyQuantity = 0;
        int totalSellQuantity = 0;

        // Calculate total buy and sell quantities
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.BUY) {
                totalBuyQuantity += transaction.getQuantity();
            } else if (transaction.getType() == TransactionType.SELL) {
                totalSellQuantity += transaction.getQuantity();
            }
        }

        // Assert that total sell quantity does not exceed total buy quantity
        assertTrue(totalSellQuantity <= totalBuyQuantity, 
                   "Total sell quantity should not exceed total buy quantity.");
    }
}

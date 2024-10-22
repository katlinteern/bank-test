package com.example.generator;

import static org.junit.jupiter.api.Assertions.*;

import com.example.enums.TransactionType;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.repository.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class TransactionGeneratorTest {

    @InjectMocks
    private TransactionGenerator transactionGenerator;

    @Mock
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void generateTransactions_whenCalled_createsExpectedNumberOfTransactions() {
        Investment mockInvestment = createMockInvestment();

        List<Transaction> transactions = transactionGenerator.generateTransactions(mockInvestment);

        assertEquals(100, transactions.size(), "There should be exactly 100 transactions generated for the investment.");
    }

    @Test
    public void generateTransactions_whenCalled_createsValidTransactionData() {
        Investment mockInvestment = createMockInvestment();

        List<Transaction> transactions = transactionGenerator.generateTransactions(mockInvestment);

        for (Transaction transaction : transactions) {
            assertNotNull(transaction.getTimestamp(), "Transaction timestamp should not be null.");
            assertTrue(transaction.getPrice().compareTo(BigDecimal.valueOf(90)) >= 0
                    && transaction.getPrice().compareTo(BigDecimal.valueOf(110)) <= 0,
                    "Transaction price should fluctuate within the +-10 range of the initial price");

            assertTrue(transaction.getQuantity() >= 0, "Transaction quantity should be non-negative");
        }
    }

    @Test
    public void generateTransactions_whenCalled_createsValidBuyAndSellQuantities() {
        Investment mockInvestment = createMockInvestment();

        List<Transaction> transactions = transactionGenerator.generateTransactions(mockInvestment);

        int totalBuyQuantity = 0;
        int totalSellQuantity = 0;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.BUY) {
                totalBuyQuantity += transaction.getQuantity();
            } else if (transaction.getType() == TransactionType.SELL) {
                totalSellQuantity += transaction.getQuantity();
            }
        }

        assertTrue(totalSellQuantity <= totalBuyQuantity, "Total sell quantity should not exceed total buy quantity.");
    }

    @Test
    public void generateTransactions_whenCalled_createsAtLeastOneTransaction() {
        Investment mockInvestment = createMockInvestment();

        List<Transaction> transactions = transactionGenerator.generateTransactions(mockInvestment);

        assertFalse(transactions.isEmpty(), "There should be some transactions generated.");
    }

    // New edge case test: Transaction fee is within valid range
    @Test
    public void generateTransactions_whenCalled_createsValidTransactionFees() {
        Investment mockInvestment = createMockInvestment();

        List<Transaction> transactions = transactionGenerator.generateTransactions(mockInvestment);

        for (Transaction transaction : transactions) {
            assertTrue(transaction.getFee().compareTo(BigDecimal.ZERO) >= 0 
                    && transaction.getFee().compareTo(BigDecimal.valueOf(10)) <= 0, 
                    "Transaction fee should be between 0 and 10");
        }
    }

    // New edge case test: Timestamps are ordered correctly
    @Test
    public void generateTransactions_whenCalled_createsChronologicallyOrderedTimestamps() {
        Investment mockInvestment = createMockInvestment();

        List<Transaction> transactions = transactionGenerator.generateTransactions(mockInvestment);

        Instant previousTimestamp = null;

        for (Transaction transaction : transactions) {
            if (previousTimestamp != null) {
                assertTrue(transaction.getTimestamp().isAfter(previousTimestamp),
                           "Transaction timestamps should be strictly increasing.");
            }
            previousTimestamp = transaction.getTimestamp();
        }
    }

    // New edge case test: Handling zero price investment
    @Test
    public void generateTransactions_withZeroPriceInvestment_shouldThrowException() {
        Investment zeroPriceInvestment = createMockInvestmentWithPrice(BigDecimal.ZERO);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionGenerator.generateTransactions(zeroPriceInvestment);
        });

        assertEquals("Investment price must be greater than zero.", exception.getMessage());
    }

    // New edge case test: Handling negative price investment
    @Test
    public void generateTransactions_withNegativePriceInvestment_shouldThrowException() {
        Investment negativePriceInvestment = createMockInvestmentWithPrice(BigDecimal.valueOf(-100));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionGenerator.generateTransactions(negativePriceInvestment);
        });

        assertEquals("Investment price must be greater than zero.", exception.getMessage());
    }

    @Test
    public void generateTransactions_withNegativePriceInvestment_shouldSetToMinimumPrice() {
        Investment smallPriceInvestment = createMockInvestmentWithPrice(BigDecimal.valueOf(1));

        List<Transaction> transactions = transactionGenerator.generateTransactions(smallPriceInvestment);

        for (Transaction transaction : transactions) {
            assertTrue(transaction.getPrice().compareTo(BigDecimal.valueOf(1)) >= 0,
                    "Transaction price should be set to the minimum value and never go below 1.");
        }
    }

    private Investment createMockInvestment() {
        Investment investment = new Investment();
        investment.setName("Fund A");
        investment.setCurrentPrice(BigDecimal.valueOf(100));
        investment.setUserId(1L);
        return investment;
    }

    // Helper method for creating an investment with a specified price
    private Investment createMockInvestmentWithPrice(BigDecimal price) {
        Investment investment = new Investment();
        investment.setName("Fund B");
        investment.setCurrentPrice(price);
        investment.setUserId(2L);
        return investment;
    }
}

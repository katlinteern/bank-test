package com.example.generator;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

        assertTrue(transactions.size() == 100, "There should be exactly 100 transactions generated for the investment.");
    }

    @Test
    public void generateTransactions_whenCalled_createsValidTransactionData() {
        Investment mockInvestment = createMockInvestment();

        List<Transaction> transactions = transactionGenerator.generateTransactions(mockInvestment);

        for (Transaction transaction : transactions) {
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

        assertTrue(totalSellQuantity <= totalBuyQuantity, 
                   "Total sell quantity should not exceed total buy quantity.");
    }

    @Test
    public void generateTransactions_whenCalled_createsAtLeastOneTransaction() {
        Investment mockInvestment = createMockInvestment();

        List<Transaction> transactions = transactionGenerator.generateTransactions(mockInvestment);

        assertTrue(transactions.size() > 0, "There should be some transactions generated.");
    }

    private Investment createMockInvestment() {
        Investment investment = new Investment();
        investment.setName("Fund A");
        investment.setCurrentPrice(BigDecimal.valueOf(100));
        investment.setUserId(1L);
        return investment;
    }
}

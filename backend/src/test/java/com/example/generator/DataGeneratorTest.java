package com.example.generator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.enums.TransactionType;
import com.example.model.Dividend;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.repository.DividendRepository;
import com.example.repository.InvestmentRepository;
import com.example.repository.TransactionRepository;

class DataGeneratorTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private DividendRepository dividendRepository;

    @InjectMocks
    private DataGenerator dataGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSalesDoNotExceedPurchases() {
        Investment investment = new Investment();
        investment.setName("Company 1");
        investment.setId(1L);

        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

        dataGenerator.run();

        List<Transaction> transactions = transactionRepository.findByInvestment(investment);
        int totalBought = transactions.stream()
                                      .filter(t -> t.getType().equals(TransactionType.BUY))
                                      .mapToInt(Transaction::getQuantity)
                                      .sum();
        int totalSold = transactions.stream()
                                    .filter(t -> t.getType().equals(TransactionType.SELL))
                                    .mapToInt(Transaction::getQuantity)
                                    .sum();

        assertTrue(totalSold <= totalBought, "Total sales should not exceed total purchases");
    }
/* 
    @Test
    void testPriceConsistencyForSingleInvestment() {
        dataGenerator.run(); // Ensure data generation is triggered
    
        List<Investment> investments = investmentRepository.findAll();
        assertFalse(investments.isEmpty(), "No investments were generated!");
    
        Investment investment = investments.get(0); // Assuming you want the first investment
        List<Transaction> transactions = transactionRepository.findByInvestment(investment);
    
        assertFalse(transactions.isEmpty(), "No transactions were generated for the investment!");
    
        Transaction firstTransaction = transactions.get(0); // Access the first transaction safely
        BigDecimal initialPrice = firstTransaction.getPrice();
    
        // Add more assertions based on the generated data
        for (Transaction transaction : transactions) {
            assertNotNull(transaction.getPrice(), "Transaction price should not be null");
            assertEquals(initialPrice, transaction.getPrice(), "Prices should be consistent");
        }
    } */
    

/*     @Test
    void testTransactionsOrderedByTime() {
        Investment investment = new Investment();
        investment.setName("Company 3");
        investment.setId(3L);

        // Mock the investment save
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

        dataGenerator.run();

        List<Transaction> transactions = transactionRepository.findByInvestment(investment);
        boolean isOrdered = true;

        for (int i = 1; i < transactions.size(); i++) {
            if (transactions.get(i).getTimestamp().isBefore(transactions.get(i - 1).getTimestamp())) {
                isOrdered = false;
                break;
            }
        }

        assertTrue(isOrdered, "Transactions should be in chronological order");
    } */

    @Test
    void testReasonableTimeGapsBetweenTransactions() {
        Investment investment = new Investment();
        investment.setName("Company 4");
        investment.setId(4L);

        // Mock the investment save
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

        dataGenerator.run();

        List<Transaction> transactions = transactionRepository.findByInvestment(investment);
        boolean reasonableGaps = true;

        for (int i = 1; i < transactions.size(); i++) {
            long gap = transactions.get(i).getTimestamp().getEpochSecond() - transactions.get(i - 1).getTimestamp().getEpochSecond();
            if (gap < 24 * 60 * 60) { // Less than 1 day gap
                reasonableGaps = false;
                break;
            }
        }

        assertTrue(reasonableGaps, "Time gap between transactions should be reasonable");
    }

    @Test
    void testDividendAndTransactionDatesWithinExtendedHorizon() {
        Investment investment = new Investment();
        investment.setName("Fund 1");
        investment.setId(5L);

        // Mock the investment save
        when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

        dataGenerator.run();

        List<Transaction> transactions = transactionRepository.findByInvestment(investment);
        List<Dividend> dividends = dividendRepository.findByInvestment(investment);

        Instant twoYearsAgo = Instant.now().minusSeconds(2L * 365 * 24 * 60 * 60);

        boolean withinHorizon = transactions.stream().allMatch(t -> t.getTimestamp().isAfter(twoYearsAgo)) &&
                                dividends.stream().allMatch(d -> d.getTimestamp().isAfter(twoYearsAgo));

        assertTrue(withinHorizon, "Transactions and Dividends should be within the 2-year horizon");
    }
}

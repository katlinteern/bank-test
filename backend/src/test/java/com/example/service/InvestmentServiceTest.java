/* package com.example.service;

import com.example.dto.CashFlowData;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.model.Dividend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class InvestmentServiceTest {
    @InjectMocks
    private InvestmentService investmentService; // Inject mocks here

    @Mock
    private TransactionService transactionService; // Mock the TransactionService

    private Investment investment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        investment = new Investment();
        
        // Initialize the investment object here as needed
        investment.setUserId(1L); // Set userId
        investment.setName("Test Investment");
        investment.setCurrentPrice(BigDecimal.valueOf(100));
        investment.setCurrentQuantity(10);
    }

    @Test
    public void collectCashFlowData_whenTransactionsAndDividendsProvided_sortedOrderReturned() {
        // Create test transactions with different timestamps
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(investment, new BigDecimal("100"), Instant.parse("2024-01-01T10:00:00Z")));
        transactions.add(new Transaction(investment, new BigDecimal("-50"), Instant.parse("2024-01-02T10:00:00Z")));
        
        // Create test dividends with different timestamps
        List<Dividend> dividends = new ArrayList<>();
        dividends.add(new Dividend(investment, new BigDecimal("20"), Instant.parse("2024-01-03T10:00:00Z")));
        dividends.add(new Dividend(investment, new BigDecimal("30"), Instant.parse("2024-01-01T09:00:00Z")));

        // Set transactions and dividends in the investment
        investment.setTransactions(transactions);
        investment.setDividends(dividends);

        // Mock the cash flow calculations for transactions
        when(transactionService.calculateCashFlow(transactions.get(0))).thenReturn(new BigDecimal("100"));
        when(transactionService.calculateCashFlow(transactions.get(1))).thenReturn(new BigDecimal("-50"));

        // Collect cash flow data
        List<CashFlowData> cashFlowData = investmentService.collectCashFlowData(investment);

        // Verify the order of cash flow data based on dates
        assertEquals(5, cashFlowData.size()); // 4 transactions and dividends plus current value
        assertEquals(Instant.parse("2024-01-01T09:00:00Z"), cashFlowData.get(0).getDate()); // Dividend from 2024-01-01
        assertEquals(Instant.parse("2024-01-01T10:00:00Z"), cashFlowData.get(1).getDate()); // Transaction from 2024-01-01
        assertEquals(Instant.parse("2024-01-02T10:00:00Z"), cashFlowData.get(2).getDate()); // Transaction from 2024-01-02
        assertEquals(Instant.parse("2024-01-03T10:00:00Z"), cashFlowData.get(3).getDate()); // Dividend from 2024-01-03

        // Ensure that the current cash flow date is now
        CashFlowData currentCashFlowData = cashFlowData.get(cashFlowData.size() - 1); // Assuming it's the last one
        assertEquals(Instant.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS), currentCashFlowData.getDate().truncatedTo(java.time.temporal.ChronoUnit.SECONDS));
    }

    @Test
    public void collectCashFlowData_whenZeroCashFlowProvided_filteredOut() {
        // Create test transactions with zero cash flow
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(investment, new BigDecimal("0"), Instant.parse("2024-01-01T10:00:00Z")));
        transactions.add(new Transaction(investment, new BigDecimal("50"), Instant.parse("2024-01-02T10:00:00Z")));
        
        // Create test dividends with a zero cash flow
        List<Dividend> dividends = new ArrayList<>();
        dividends.add(new Dividend(investment, new BigDecimal("0"), Instant.parse("2024-01-03T10:00:00Z")));
        dividends.add(new Dividend(investment, new BigDecimal("30"), Instant.parse("2024-01-01T09:00:00Z")));

        // Set transactions and dividends in the investment
        investment.setTransactions(transactions);
        investment.setDividends(dividends);

        // Mock the cash flow calculations for transactions
        when(transactionService.calculateCashFlow(transactions.get(0))).thenReturn(new BigDecimal("0"));
        when(transactionService.calculateCashFlow(transactions.get(1))).thenReturn(new BigDecimal("50"));

        // Collect cash flow data
        List<CashFlowData> cashFlowData = investmentService.collectCashFlowData(investment);

        // Verify that the zero cash flows are filtered out
        assertEquals(3, cashFlowData.size()); // 3 valid cash flows (one transaction + two dividends with non-zero value)
        assertEquals(Instant.parse("2024-01-01T09:00:00Z"), cashFlowData.get(0).getDate()); // Dividend from 2024-01-01
        assertEquals(Instant.parse("2024-01-02T10:00:00Z"), cashFlowData.get(1).getDate()); // Transaction from 2024-01-02
        assertEquals(Instant.parse("2024-01-03T10:00:00Z"), cashFlowData.get(2).getDate()); // Dividend from 2024-01-03
    }
}
 */
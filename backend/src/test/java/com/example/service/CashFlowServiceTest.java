package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.dto.CashFlowData;
import com.example.enums.TransactionType;
import com.example.model.Dividend;
import com.example.model.Investment;
import com.example.model.Transaction;

class CashFlowServiceTest {

    @InjectMocks
    private CashFlowService cashFlowService;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(transactionService.calculateTotalQuantity(any())).thenReturn(10);
    }

    // Helper methods for test setup
    private Investment createInvestment(BigDecimal currentPrice, List<Transaction> transactions,
            List<Dividend> dividends) {
        Investment investment = new Investment();
        investment.setCurrentPrice(currentPrice);
        investment.setTransactions(transactions);
        investment.setDividends(dividends);
        return investment;
    }

    private Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(10);
        transaction.setPrice(BigDecimal.valueOf(100));
        transaction.setFee(BigDecimal.valueOf(5));
        transaction.setTimestamp(Instant.now().minusSeconds(7200)); // 2 hours ago
        when(transactionService.calculateCashFlow(transaction)).thenReturn(BigDecimal.valueOf(95)); // (100 * 10) - 5
        return transaction;
    }

    private Dividend createDividend() {
        Dividend dividend = new Dividend();
        dividend.setAmount(BigDecimal.valueOf(20));
        dividend.setTimestamp(Instant.now().minusSeconds(3600)); // 1 hour ago
        return dividend;
    }

    // Tests for collectAndFilterCashFlows
    @Test
    public void collectAndFilterCashFlows_EmptyInvestmentList_ReturnsEmptyList() {
        List<CashFlowData> cashFlowData = cashFlowService.collectAndFilterCashFlows(Collections.emptyList());
        
        assertTrue(cashFlowData.isEmpty(), "Expected empty list when no investments are provided");
    }

    @Test
    public void collectAndFilterCashFlows_NoValidCashFlows_ReturnsEmptyList() {
        Investment investment = createInvestment(BigDecimal.ZERO, Collections.emptyList(), Collections.emptyList());
        List<CashFlowData> cashFlowData = cashFlowService.collectAndFilterCashFlows(List.of(investment));
        
        assertTrue(cashFlowData.isEmpty(), "Expected empty list when no valid cash flows are present");
    }

    @Test
    public void collectAndFilterCashFlows_WithInvalidCashFlows_ReturnsFilteredCashFlows() {
        Investment validInvestment = createInvestment(BigDecimal.valueOf(10), List.of(createTransaction()), List.of(createDividend()));
        Investment invalidInvestment = createInvestment(BigDecimal.ZERO, Collections.emptyList(), Collections.emptyList());

        List<CashFlowData> cashFlowData = cashFlowService.collectAndFilterCashFlows(List.of(validInvestment, invalidInvestment));
        
        assertEquals(3, cashFlowData.size(), "Expected 3 valid cash flows from the valid investment only");
        assertTrue(cashFlowData.stream().anyMatch(data -> data.getAmount().equals(BigDecimal.valueOf(95))));
        assertTrue(cashFlowData.stream().anyMatch(data -> data.getAmount().equals(BigDecimal.valueOf(20))));
        assertTrue(cashFlowData.stream().anyMatch(data -> data.getAmount().equals(BigDecimal.valueOf(100))));
    }

    // Tests for collectCashFlowData
    @Test
    public void collectCashFlowData_NoTransactionsOrDividends_ReturnsOneCashFlow() {
        Investment investment = createInvestment(BigDecimal.valueOf(1), Collections.emptyList(),
                Collections.emptyList());

        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);

        assertEquals(1, cashFlowData.size(), "Expected no cash flow data for empty investment");
    }

    @Test
    public void collectCashFlowData_WithTransactionsAndDividends_ReturnsCombinedCashFlowData() {
        Investment investment = createInvestment(BigDecimal.valueOf(10), List.of(createTransaction()),
                List.of(createDividend()));

        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);

        assertEquals(3, cashFlowData.size(), "Expected 3 cash flow data entries");

        assertTrue(cashFlowData.stream().anyMatch(data -> data.getAmount().equals(BigDecimal.valueOf(95))),
                "Missing calculated cash flow from transaction");
        assertTrue(cashFlowData.stream().anyMatch(data -> data.getAmount().equals(BigDecimal.valueOf(20))),
                "Missing dividend cash flow");
        assertTrue(cashFlowData.stream().anyMatch(data -> data.getAmount().equals(BigDecimal.valueOf(100))),
                "Missing current value cash flow");
    }

    @Test
    public void collectCashFlowData_NoCurrentPrice_ReturnsNoCashFlows() {
        Investment investment = createInvestment(BigDecimal.valueOf(0), List.of(createTransaction()),
                List.of(createDividend()));

        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);

        assertEquals(0, cashFlowData.size(), "Expected no cash flow data when current price is null");
    }

    @Test
    public void collectCashFlowData_CurrentPriceZero_ReturnsNoCashFlows() {
        Investment investment = createInvestment(BigDecimal.ZERO, List.of(createTransaction()),
                List.of(createDividend()));

        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);

        assertEquals(0, cashFlowData.size(), "Expected no cash flow data when current price is zero");
    }

    @Test
    public void collectCashFlowData_MultipleTransactionsAndDividends_ReturnsCombinedCashFlowData() {
        Investment investment = createInvestment(
                BigDecimal.valueOf(10),
                List.of(createTransaction(), createTransaction()),
                List.of(createDividend(), createDividend()));

        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);

        // Each transaction and dividend will add to cash flow
        assertEquals(5, cashFlowData.size(), "Expected 5 cash flow data entries");
    }

    // Tests for extractDates
    @Test
    public void extractDates_ValidCashFlowData_ReturnsListOfDates() {
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.valueOf(100), Instant.now());
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), Instant.now().minusSeconds(3600));
        List<CashFlowData> cashFlowDataList = List.of(cashFlow1, cashFlow2);

        List<Instant> dates = cashFlowService.extractDates(cashFlowDataList);

        assertEquals(2, dates.size(), "Expected list of dates matching the cash flows");
        assertTrue(dates.contains(cashFlow1.getDate()), "Expected to find the first cash flow date");
        assertTrue(dates.contains(cashFlow2.getDate()), "Expected to find the second cash flow date");
    }

    @Test
    public void extractDates_EmptyList_ReturnsEmptyList() {
        List<CashFlowData> cashFlowDataList = Collections.emptyList();
        List<Instant> dates = cashFlowService.extractDates(cashFlowDataList);
        assertTrue(dates.isEmpty(), "Expected an empty list of dates from empty input");
    }

    // Tests for extractCashFlows
    @Test
    public void extractCashFlows_ValidCashFlowData_ReturnsListOfAmounts() {
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.valueOf(100), Instant.now());
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), Instant.now().minusSeconds(3600));
        List<CashFlowData> cashFlowDataList = List.of(cashFlow1, cashFlow2);

        List<BigDecimal> amounts = cashFlowService.extractCashFlows(cashFlowDataList);

        assertEquals(2, amounts.size(), "Expected list of amounts matching the cash flows");
        assertTrue(amounts.contains(cashFlow1.getAmount()), "Expected to find the first cash flow amount");
        assertTrue(amounts.contains(cashFlow2.getAmount()), "Expected to find the second cash flow amount");
    }

    @Test
    public void extractCashFlows_EmptyList_ReturnsEmptyList() {
        List<CashFlowData> cashFlowDataList = Collections.emptyList();
        List<BigDecimal> amounts = cashFlowService.extractCashFlows(cashFlowDataList);
        assertTrue(amounts.isEmpty(), "Expected an empty list of amounts from empty input");
    }

}

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

    // Tests for collectCashFlowData
    @Test
    public void collectCashFlowData_NoTransactionsOrDividends_ReturnsNoCashFlows() {
        Investment investment = new Investment();
        investment.setCurrentPrice(null);
        investment.setTransactions(Collections.emptyList());
        investment.setDividends(Collections.emptyList());

        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);

        assertEquals(0, cashFlowData.size(), "Expected no cash flow data for empty investment");
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

        assertEquals(BigDecimal.valueOf(100), cashFlowData.get(2).getAmount(),
                "The last entry should be the current value cash flow");
    }

    @Test
    public void collectCashFlowData_NoCurrentPrice_ReturnsNoCashFlows() {
        Investment investment = createInvestment(null, List.of(createTransaction()), List.of(createDividend()));
        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);

        assertEquals(0, cashFlowData.size(), "Expected no cash flow data when current price is null");
    }

    @Test
    public void collectCashFlowData_CurrentPriceZero_ReturnsNoCashFlows() {
        Investment investment = createInvestment(BigDecimal.ZERO, List.of(createTransaction()), List.of(createDividend()));
        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);
    
        assertEquals(0, cashFlowData.size(), "Expected no cash flow data when current price is zero");
    }

    // Tests for filterAndSortCashFlowData
    @Test
    public void filterAndSortCashFlowData_NoCashFlows_ReturnsEmptyList() {
        List<CashFlowData> cashFlowData = Collections.emptyList();
        List<CashFlowData> filteredData = cashFlowService.filterAndSortCashFlowData(cashFlowData);
        assertTrue(filteredData.isEmpty(), "Expected empty list when no cash flows present");
    }

    @Test
    public void filterAndSortCashFlowData_WithFutureCashFlows_ReturnsFilteredData() {
        Instant now = Instant.now();
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.valueOf(100), now.plusSeconds(3600)); // 1 hour in the
                                                                                                   // future
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), now.minusSeconds(7200)); // 2 hours ago
        List<CashFlowData> cashFlowData = List.of(cashFlow1, cashFlow2);

        List<CashFlowData> sortedData = cashFlowService.filterAndSortCashFlowData(cashFlowData);

        assertEquals(1, sortedData.size(), "Expected only past cash flow data to remain");
        assertEquals(cashFlow2.getDate(), sortedData.get(0).getDate(), "Expected past cash flow to be the only item");
    }

    @Test
    public void filterAndSortCashFlowData_WithZeroAmountCashFlows_ReturnsFilteredData() {
        Instant now = Instant.now();
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.ZERO, now.minusSeconds(3600)); // 1 hour ago
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), now.minusSeconds(7200)); // 2 hours ago
        List<CashFlowData> cashFlowData = List.of(cashFlow1, cashFlow2);

        List<CashFlowData> sortedData = cashFlowService.filterAndSortCashFlowData(cashFlowData);

        assertEquals(1, sortedData.size(), "Expected only non-zero cash flow data");
        assertEquals(cashFlow2.getDate(), sortedData.get(0).getDate(), "Expected positive cash flow to remain");
    }

    @Test
    public void filterAndSortCashFlowData_ValidCashFlows_ReturnsSortedData() {
        Instant now = Instant.now();
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.valueOf(100), now.minusSeconds(3600));
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), now.minusSeconds(7200));
        List<CashFlowData> cashFlowData = List.of(cashFlow1, cashFlow2);

        List<CashFlowData> sortedData = cashFlowService.filterAndSortCashFlowData(cashFlowData);

        assertEquals(2, sortedData.size(), "Expected sorted cash flow data");
        assertEquals(cashFlow2.getDate(), sortedData.get(0).getDate(), "Expected the earliest cash flow first");
        assertEquals(cashFlow1.getDate(), sortedData.get(1).getDate(), "Expected the later cash flow second");
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
}

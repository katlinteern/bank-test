package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
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
    }

    // Tests for collectCashFlowData
    @Test
    public void collectCashFlowData_ValidInvestment_ReturnsCashFlowData() {
        Investment investment = new Investment();
        investment.setCurrentPrice(BigDecimal.valueOf(10));
        investment.setCurrentQuantity(5);
        investment.setTransactions(Collections.emptyList());
        investment.setDividends(Collections.emptyList());

        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);

        assertEquals(1, cashFlowData.size());
        assertEquals(BigDecimal.valueOf(50), cashFlowData.get(0).getAmount());
    }

    @Test
    public void collectCashFlowData_NoTransactionsOrDividends_ReturnsCurrentValueOnly() {
        Investment investment = new Investment();
        investment.setCurrentPrice(BigDecimal.valueOf(10));
        investment.setCurrentQuantity(5);
        investment.setTransactions(Collections.emptyList());
        investment.setDividends(Collections.emptyList());

        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);

        assertEquals(1, cashFlowData.size());
        assertEquals(BigDecimal.valueOf(50), cashFlowData.get(0).getAmount());
    }

    @Test
    public void collectCashFlowData_WithTransactionsAndDividends_ReturnsCombinedCashFlowData() {
        Investment investment = new Investment();
        investment.setCurrentPrice(BigDecimal.valueOf(10));
        investment.setCurrentQuantity(5);

        // Mocking a transaction
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY); 
        transaction.setQuantity(10); 
        transaction.setPrice(BigDecimal.valueOf(100)); 
        transaction.setFee(BigDecimal.valueOf(5)); 
        transaction.setTimestamp(Instant.now().minusSeconds(7200)); // 2 hours ago

        when(transactionService.calculateCashFlow(transaction)).thenReturn(BigDecimal.valueOf(95)); // (100 * 10) - 5

         // Mocking a dividend
        Dividend dividend = new Dividend();
        dividend.setAmount(BigDecimal.valueOf(20)); 
        dividend.setTimestamp(Instant.now().minusSeconds(3600)); // 1 hour ago

        investment.setTransactions(List.of(transaction));
        investment.setDividends(List.of(dividend));

        List<CashFlowData> cashFlowData = cashFlowService.collectCashFlowData(investment);

        assertEquals(3, cashFlowData.size());
        assertTrue(cashFlowData.stream().anyMatch(data -> data.getAmount().equals(BigDecimal.valueOf(95))));
        assertTrue(cashFlowData.stream().anyMatch(data -> data.getAmount().equals(BigDecimal.valueOf(20))));
        assertTrue(cashFlowData.stream().anyMatch(data -> data.getAmount().equals(BigDecimal.valueOf(50))));
    }

    // Tests for filterAndSortCashFlowData
    @Test
    public void filterAndSortCashFlowData_NoCashFlows_ReturnsEmptyList() {
        List<CashFlowData> cashFlowData = Collections.emptyList();
        List<CashFlowData> filteredData = cashFlowService.filterAndSortCashFlowData(cashFlowData);

        assertTrue(filteredData.isEmpty());
    }

    @Test
    public void filterAndSortCashFlowData_WithFutureCashFlows_ReturnsFilteredData() {
        Instant now = Instant.now();
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.valueOf(100), now.plusSeconds(3600)); // 1 hour in the future
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), now.minusSeconds(7200)); // 2 hours ago
        List<CashFlowData> cashFlowData = List.of(cashFlow1, cashFlow2);

        List<CashFlowData> sortedData = cashFlowService.filterAndSortCashFlowData(cashFlowData);

        assertEquals(1, sortedData.size());
        assertEquals(cashFlow2.getDate(), sortedData.get(0).getDate());
    }

    @Test
    public void filterAndSortCashFlowData_WithZeroAmountCashFlows_ReturnsFilteredData() {
        Instant now = Instant.now();
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.ZERO, now.minusSeconds(3600)); // 1 hour ago
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), now.minusSeconds(7200)); // 2 hours ago
        List<CashFlowData> cashFlowData = List.of(cashFlow1, cashFlow2);

        List<CashFlowData> sortedData = cashFlowService.filterAndSortCashFlowData(cashFlowData);

        assertEquals(1, sortedData.size());
        assertEquals(cashFlow2.getDate(), sortedData.get(0).getDate());
    }

    @Test
    public void filterAndSortCashFlowData_ValidCashFlows_ReturnsSortedData() {
        Instant now = Instant.now();
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.valueOf(100), now.minusSeconds(3600));
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), now.minusSeconds(7200));
        List<CashFlowData> cashFlowData = List.of(cashFlow1, cashFlow2);

        List<CashFlowData> sortedData = cashFlowService.filterAndSortCashFlowData(cashFlowData);

        assertEquals(2, sortedData.size());
        assertEquals(cashFlow2.getDate(), sortedData.get(0).getDate());
        assertEquals(cashFlow1.getDate(), sortedData.get(1).getDate());
    }

    // Tests for extractDates
    @Test
    public void extractDates_ValidCashFlowData_ReturnsListOfDates() {
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.valueOf(100), Instant.now());
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), Instant.now().minusSeconds(3600));
        List<CashFlowData> cashFlowDataList = List.of(cashFlow1, cashFlow2);

        List<Instant> dates = cashFlowService.extractDates(cashFlowDataList);

        assertEquals(2, dates.size());
        assertTrue(dates.contains(cashFlow1.getDate()));
        assertTrue(dates.contains(cashFlow2.getDate()));
    }

    @Test
    public void extractDates_EmptyList_ReturnsEmptyList() {
        List<CashFlowData> cashFlowDataList = Collections.emptyList();
        List<Instant> dates = cashFlowService.extractDates(cashFlowDataList);
        assertTrue(dates.isEmpty());
    }

    // Tests for extractCashFlows
    @Test
    public void extractCashFlows_ValidCashFlowData_ReturnsListOfAmounts() {
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.valueOf(100), Instant.now());
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), Instant.now().minusSeconds(3600));
        List<CashFlowData> cashFlowDataList = List.of(cashFlow1, cashFlow2);

        List<BigDecimal> amounts = cashFlowService.extractCashFlows(cashFlowDataList);

        assertEquals(2, amounts.size());
        assertTrue(amounts.contains(cashFlow1.getAmount()));
        assertTrue(amounts.contains(cashFlow2.getAmount()));
    }

    @Test
    public void extractCashFlows_EmptyList_ReturnsEmptyList() {
        List<CashFlowData> cashFlowDataList = Collections.emptyList();
        List<BigDecimal> amounts = cashFlowService.extractCashFlows(cashFlowDataList);
        assertTrue(amounts.isEmpty());
    }
}

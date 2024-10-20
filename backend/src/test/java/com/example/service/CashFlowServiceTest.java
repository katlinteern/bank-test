package com.example.service;

import static org.junit.jupiter.api.Assertions.*;

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
import com.example.model.Investment;

class CashFlowServiceTest {

    @InjectMocks
    private CashFlowService cashFlowService;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

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
    public void filterAndSortCashFlowData_NoCashFlows_ReturnsEmptyList() {
        List<CashFlowData> cashFlowData = Collections.emptyList();
        List<CashFlowData> filteredData = cashFlowService.filterAndSortCashFlowData(cashFlowData);

        assertTrue(filteredData.isEmpty());
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
    public void extractCashFlows_ValidCashFlowData_ReturnsListOfAmounts() {
        CashFlowData cashFlow1 = new CashFlowData(BigDecimal.valueOf(100), Instant.now());
        CashFlowData cashFlow2 = new CashFlowData(BigDecimal.valueOf(200), Instant.now().minusSeconds(3600));
        List<CashFlowData> cashFlowDataList = List.of(cashFlow1, cashFlow2);

        List<BigDecimal> amounts = cashFlowService.extractCashFlows(cashFlowDataList);

        assertEquals(2, amounts.size());
        assertTrue(amounts.contains(cashFlow1.getAmount()));
        assertTrue(amounts.contains(cashFlow2.getAmount()));
    }
}

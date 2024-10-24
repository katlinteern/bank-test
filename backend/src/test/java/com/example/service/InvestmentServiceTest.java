package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.dto.CashFlowData;
import com.example.dto.InvestmentResponse;
import com.example.dto.InvestmentSummaryResponse;
import com.example.model.Dividend;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.enums.TransactionType;
import com.example.repository.InvestmentRepository;
import com.example.util.XirrCalculator;

class InvestmentServiceTest {

    @InjectMocks
    private InvestmentService investmentService;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private CashFlowService cashFlowService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private XirrCalculator xirrCalculator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper methods for test setup
    private Investment createInvestment(BigDecimal currentPrice, List<Transaction> transactions,
            List<Dividend> dividends) {
        Investment investment = new Investment();
        investment.setUserId(1L);
        investment.setName("Name 1");
        investment.setCurrentPrice(currentPrice);
        investment.setTransactions(transactions);
        investment.setDividends(dividends);
        return investment;
    }

    private Transaction createTransaction() {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(5);
        transaction.setPrice(BigDecimal.valueOf(10));
        transaction.setFee(BigDecimal.valueOf(5));
        transaction.setTimestamp(Instant.now().minusSeconds(7200)); // 2 hours ago
        return transaction;
    }

    private Dividend createDividend(BigDecimal amount) {
        Dividend dividend = new Dividend();
        dividend.setAmount(amount);
        dividend.setTimestamp(Instant.now().minusSeconds(3600)); // 1 hour ago
        return dividend;
    }

    // Test for getUserInvestments
    @Test
    public void getUserInvestments_UserExistsNoInvestments_ReturnsEmptyList() {
        Long userId = 1L;

        when(investmentRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        List<InvestmentResponse> investments = investmentService.getUserInvestments(userId);

        assertTrue(investments.isEmpty());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getUserInvestments_UserExistsWithInvestments_ReturnsInvestments() {
        Long userId = 1L;
        Investment investment = createInvestment(BigDecimal.valueOf(12), List.of(createTransaction()), Collections.emptyList());

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment));
        when(transactionService.calculateTotalQuantity(any())).thenReturn(5);

        List<InvestmentResponse> investments = investmentService.getUserInvestments(userId);

        assertEquals(1, investments.size());
        assertEquals(investment.getName(), investments.get(0).getName());
        assertEquals(BigDecimal.valueOf(60), investments.get(0).getTotalValue()); // Total value (12 * 5)
        assertEquals(investment.getCurrentPrice(), investments.get(0).getCurrentPrice());
        assertEquals(5, investments.get(0).getQuantity());

        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getUserInvestments_WithDividends_ReturnsInvestmentsWithDividends() {
        Long userId = 1L;
        Dividend dividend = createDividend(BigDecimal.valueOf(100));
        Investment investment = createInvestment(BigDecimal.valueOf(12), List.of(createTransaction()), List.of(dividend));

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment));
        when(transactionService.calculateTotalQuantity(any())).thenReturn(5);

        List<InvestmentResponse> investments = investmentService.getUserInvestments(userId);

        assertEquals(1, investments.size());
        assertEquals(investment.getName(), investments.get(0).getName());
        assertEquals(BigDecimal.valueOf(60), investments.get(0).getTotalValue()); // Total value (12 * 5)
        assertEquals(investment.getCurrentPrice(), investments.get(0).getCurrentPrice());
        assertEquals(5, investments.get(0).getQuantity());

        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    // Test for getUserInvestmentSummary
    @Test
    public void getUserInvestmentSummary_UserExistsNoInvestments_ReturnsEmptySummary() {
        Long userId = 1L;
        when(investmentRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertNull(summary.getTotalValue());
        assertNull(summary.getProfitability());
        assertEquals(0, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getUserInvestmentSummary_WithOneInvestmentAndDividend_ReturnsCorrectSummary() {
        Long userId = 1L;
        Transaction transaction = createTransaction();
        Dividend dividend = createDividend(BigDecimal.valueOf(100));
        Investment investment = createInvestment(BigDecimal.valueOf(10), List.of(transaction), List.of(dividend));

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment));
        when(transactionService.calculateTotalQuantity(any())).thenReturn(5);
        when(cashFlowService.collectAndFilterCashFlows(any())).thenReturn(Collections.emptyList());

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(BigDecimal.valueOf(50), summary.getTotalValue());
        assertNull(summary.getProfitability());
        assertEquals(1, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getUserInvestmentSummary_WithTwoInvestments_ReturnsCorrectSummary() {
        Long userId = 1L;
        Investment investment1 = createInvestment(BigDecimal.valueOf(10), Collections.emptyList(), Collections.emptyList());
        Investment investment2 = createInvestment(BigDecimal.valueOf(20), Collections.emptyList(), Collections.emptyList());

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment1, investment2));
        when(transactionService.calculateTotalQuantity(any())).thenReturn(5);

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(BigDecimal.valueOf(150), summary.getTotalValue());
        assertNull(summary.getProfitability());
        assertEquals(2, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    // Test for calculateTotalValue
    @Test
    public void calculateTotalValue_WithValidInvestment_ReturnsCorrectTotal() {
        Investment investment = createInvestment(BigDecimal.valueOf(20), List.of(createTransaction()), Collections.emptyList());

        when(transactionService.calculateTotalQuantity(investment.getTransactions())).thenReturn(5);

        BigDecimal totalValue = investmentService.calculateTotalValue(investment);

        assertEquals(BigDecimal.valueOf(100), totalValue); // 20 * 5
    }

    @Test
    public void calculateTotalValue_WithNullInvestment_ReturnsZero() {
        BigDecimal totalValue = investmentService.calculateTotalValue(null);

        assertEquals(BigDecimal.ZERO, totalValue);
    }

    // Test for calculateProfitability
    @Test
    public void calculateProfitability_EmptyCashFlowData_ReturnsNull() {
        List<CashFlowData> emptyCashFlowData = Collections.emptyList();

        BigDecimal result = investmentService.calculateProfitability(emptyCashFlowData);

        assertNull(result);
        verifyNoInteractions(xirrCalculator);
    }

    @Test
    public void calculateProfitability_WithValidXirr_ReturnsProfitability() {
        List<CashFlowData> cashFlowData = List.of(
                new CashFlowData(BigDecimal.valueOf(-100), Instant.now()),
                new CashFlowData(BigDecimal.valueOf(150), Instant.now().minusSeconds(3600))
        );

        when(cashFlowService.extractDates(cashFlowData)).thenReturn(List.of(Instant.now(), Instant.now().minusSeconds(3600)));
        when(cashFlowService.extractCashFlows(cashFlowData)).thenReturn(List.of(BigDecimal.valueOf(-100), BigDecimal.valueOf(150)));
        when(xirrCalculator.calculateXirr(anyList(), anyList())).thenReturn(BigDecimal.valueOf(0.1)); // XIRR of 10%

        BigDecimal result = investmentService.calculateProfitability(cashFlowData);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP), result); // Profitability of 10%
        verify(xirrCalculator, times(1)).calculateXirr(anyList(), anyList());
    }

    @Test
    public void calculateProfitability_WithXirrException_ReturnsNull() {
        List<CashFlowData> cashFlowData = List.of(
                new CashFlowData(BigDecimal.valueOf(-100), Instant.now()),
                new CashFlowData(BigDecimal.valueOf(150), Instant.now().minusSeconds(3600))
        );

        when(cashFlowService.extractDates(cashFlowData)).thenReturn(List.of(Instant.now(), Instant.now().minusSeconds(3600)));
        when(cashFlowService.extractCashFlows(cashFlowData)).thenReturn(List.of(BigDecimal.valueOf(-100), BigDecimal.valueOf(150)));
        when(xirrCalculator.calculateXirr(anyList(), anyList())).thenThrow(new IllegalArgumentException("Invalid data"));

        BigDecimal result = investmentService.calculateProfitability(cashFlowData);

        assertNull(result);
        verify(xirrCalculator, times(1)).calculateXirr(anyList(), anyList());
    }
}

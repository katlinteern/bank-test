package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.dto.InvestmentResponse;
import com.example.dto.InvestmentSummaryResponse;
import com.example.model.Dividend;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.enums.TransactionType;
import com.example.repository.InvestmentRepository;

class InvestmentServiceTest {

    @InjectMocks
    private InvestmentService investmentService;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private CashFlowService cashFlowService;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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

    private Transaction createTransaction(TransactionType type, int quantity, BigDecimal price, BigDecimal fee) {
        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setQuantity(quantity);
        transaction.setPrice(price);
        transaction.setFee(fee);
        transaction.setTimestamp(Instant.now().minusSeconds(7200)); // 2 hours ago
        when(transactionService.calculateCashFlow(transaction))
                .thenReturn(price.subtract(fee).multiply(BigDecimal.valueOf(quantity))); // Total cash flow after fee
        return transaction;
    }

    private Dividend createDividend(BigDecimal amount) {
        Dividend dividend = new Dividend();
        dividend.setAmount(amount);
        dividend.setTimestamp(Instant.now().minusSeconds(3600)); // 1 hour ago
        return dividend;
    }

    // getUserInvestments

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
        Investment investment = createInvestment(BigDecimal.valueOf(10), Collections.emptyList(),
                Collections.emptyList());
        investment.setId(1L);
        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment));
        when(transactionService.calculateTotalQuantity(any())).thenReturn(5);

        List<InvestmentResponse> investments = investmentService.getUserInvestments(userId);

        assertEquals(1, investments.size());
        assertEquals(investment.getId(), investments.get(0).getId());
        assertEquals(BigDecimal.valueOf(50), investments.get(0).getTotalValue());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getUserInvestments_InvalidUserId_ReturnsEmptyList() {
        Long invalidUserId = -1L;
        when(investmentRepository.findAllByUserId(invalidUserId)).thenReturn(Collections.emptyList());

        List<InvestmentResponse> investments = investmentService.getUserInvestments(invalidUserId);

        assertTrue(investments.isEmpty());
        verify(investmentRepository, times(1)).findAllByUserId(invalidUserId);
    }

    // getUserInvestmentSummary

    @Test
    public void getUserInvestmentSummary_UserExistsNoInvestments_ReturnsEmptySummary() {
        Long userId = 1L;
        when(investmentRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(null, summary.getTotalValue());
        assertEquals(null, summary.getProfitability());
        assertEquals(0, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getUserInvestmentSummary_UserExistsWithInvestments_ReturnsSummary() {
        Long userId = 1L;
        Transaction transaction = createTransaction(TransactionType.BUY, 10, BigDecimal.valueOf(10),
                BigDecimal.valueOf(5));
        Investment investment = createInvestment(BigDecimal.valueOf(10), List.of(transaction), Collections.emptyList());

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment));
        when(transactionService.calculateTotalQuantity(any())).thenReturn(10);
        when(cashFlowService.collectAndFilterCashFlows(any())).thenReturn(Collections.emptyList());

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(BigDecimal.valueOf(100), summary.getTotalValue());
        assertEquals(null, summary.getProfitability());
        assertEquals(1, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getUserInvestmentSummary_MultipleInvestments_ReturnsCorrectSummary() {
        Long userId = 1L;

        Investment investment1 = createInvestment(BigDecimal.valueOf(10), Collections.emptyList(),
                Collections.emptyList());
        Investment investment2 = createInvestment(BigDecimal.valueOf(20), Collections.emptyList(),
                Collections.emptyList());

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment1, investment2));
        when(transactionService.calculateTotalQuantity(any())).thenReturn(5);

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(BigDecimal.valueOf(150), summary.getTotalValue());
        assertEquals(null, summary.getProfitability());
        assertEquals(2, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }
/* 
    @Test
    public void getUserInvestmentSummary_NoCashFlowData_ReturnsNullXIRR() {
        Long userId = 1L;
        Investment investment = createInvestment(BigDecimal.valueOf(10), Collections.emptyList(),
                Collections.emptyList());

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment));
        when(transactionService.calculateTotalQuantity(any())).thenReturn(5);
        when(cashFlowService.collectAndFilterCashFlows(any())).thenReturn(Collections.emptyList());
    
        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(BigDecimal.valueOf(50), summary.getTotalValue());
        assertEquals(null, summary.getProfitability());
        assertEquals(1, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }
 */
    @Test
    public void getUserInvestmentSummary_MultipleInvestmentsWithZeroPrices_ReturnsNullSummary() {
        Long userId = 1L;

        Investment investment1 = createInvestment(BigDecimal.ZERO, Collections.emptyList(), Collections.emptyList());
        Investment investment2 = createInvestment(BigDecimal.ZERO, Collections.emptyList(), Collections.emptyList());

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment1, investment2));
        when(transactionService.calculateTotalQuantity(any())).thenReturn(5);

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(BigDecimal.ZERO, summary.getTotalValue());
        assertEquals(null, summary.getProfitability());
        assertEquals(2, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

}

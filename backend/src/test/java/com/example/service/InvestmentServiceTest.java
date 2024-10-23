package com.example.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.dto.InvestmentResponse;
import com.example.dto.InvestmentSummaryResponse;
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
        when(transactionService.calculateTotalQuantity(any())).thenReturn(5);
    }

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
        Investment investment = new Investment();
        investment.setId(1L);
        investment.setName("Investment A");
        investment.setCurrentPrice(BigDecimal.valueOf(10));

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(5);
        investment.setTransactions(List.of(transaction));

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment));

        List<InvestmentResponse> investments = investmentService.getUserInvestments(userId);

        assertEquals(1, investments.size());
        assertEquals("Investment A", investments.get(0).getName());
        assertEquals(BigDecimal.valueOf(50), investments.get(0).getTotalValue());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getUserInvestmentSummary_UserExistsNoInvestments_ReturnsEmptySummary() {
        Long userId = 1L;
        when(investmentRepository.findAllByUserId(userId)).thenReturn(Collections.emptyList());

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(BigDecimal.ZERO, summary.getTotalValue());
        assertEquals(BigDecimal.ZERO, summary.getProfitability());
        assertEquals(0, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void getUserInvestmentSummary_UserExistsWithInvestments_ReturnsSummary() {
        Long userId = 1L;
        Investment investment = new Investment();
        investment.setCurrentPrice(BigDecimal.valueOf(10));

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(5);
        investment.setTransactions(List.of(transaction));
        investment.setDividends(Collections.emptyList());

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment));
        when(cashFlowService.extractCashFlows(any())).thenReturn(Collections.emptyList()); // Mock cash flow extraction

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(BigDecimal.valueOf(50), summary.getTotalValue());
        assertEquals(0, summary.getProfitability().compareTo(BigDecimal.ZERO));
        assertEquals(1, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    public void createInvestmentResponse_ValidInvestment_ReturnsInvestmentResponse() {
        Investment investment = new Investment();
        investment.setId(1L);
        investment.setName("Investment A");
        investment.setCurrentPrice(BigDecimal.valueOf(10));

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(5);
        investment.setTransactions(List.of(transaction));

        InvestmentResponse response = investmentService.createInvestmentResponse(investment);

        assertEquals(1L, response.getId());
        assertEquals("Investment A", response.getName());
        assertEquals(BigDecimal.valueOf(50), response.getTotalValue());
    }

    @Test
    public void calculateTotalValue_ValidInvestment_ReturnsCorrectTotalValue() {
        Investment investment = new Investment();
        investment.setCurrentPrice(BigDecimal.valueOf(10));

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(5);
        investment.setTransactions(List.of(transaction));

        BigDecimal totalValue = investmentService.calculateTotalValue(investment);

        assertEquals(BigDecimal.valueOf(50), totalValue);
    }

/*     @Test
    public void getUserInvestmentSummary_MultipleInvestments_ReturnsCorrectSummary() {
        Long userId = 1L;

        Investment investment1 = new Investment();
        investment1.setCurrentPrice(BigDecimal.valueOf(10));
        investment1.setUserId(userId);

        Transaction transaction1 = new Transaction();
        transaction1.setInvestment(investment1);
        transaction1.setType(TransactionType.BUY);
        transaction1.setQuantity(5);
        investment1.setTransactions(List.of(transaction1));

        Investment investment2 = new Investment();
        investment2.setCurrentPrice(BigDecimal.valueOf(20));
        investment2.setUserId(userId);

        Transaction transaction2 = new Transaction();
        transaction2.setInvestment(investment2);
        transaction2.setType(TransactionType.BUY);
        transaction2.setQuantity(3);
        investment2.setTransactions(List.of(transaction2));

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment1, investment2));

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(BigDecimal.valueOf(90), summary.getTotalValue()); // (10*5 + 20*3)
        assertEquals(0, summary.getProfitability().compareTo(BigDecimal.ZERO)); // Assuming no dividends
        assertEquals(2, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }
 */
    @Test
    public void getUserInvestmentSummary_NoCashFlowData_ReturnsZeroXIRR() {
        Long userId = 1L;
        Investment investment = new Investment();
        investment.setCurrentPrice(BigDecimal.valueOf(10));

        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(5);
        investment.setTransactions(List.of(transaction));

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment));
        when(cashFlowService.extractCashFlows(any())).thenReturn(Collections.emptyList()); // Mock cash flow extraction

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        assertEquals(BigDecimal.valueOf(50), summary.getTotalValue());
        assertEquals(0, summary.getProfitability().compareTo(BigDecimal.ZERO)); // Assuming no cash flows
        assertEquals(1, summary.getNumberOfInvestments());
        verify(investmentRepository, times(1)).findAllByUserId(userId);
    }

}

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
import com.example.repository.InvestmentRepository;

class InvestmentServiceTest {

    @InjectMocks
    private InvestmentService investmentService;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private CashFlowService cashFlowService; 

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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
        investment.setCurrentQuantity(5);
        
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
        investment.setCurrentQuantity(5);
        investment.setTransactions(Collections.emptyList());
        investment.setDividends(Collections.emptyList());

        when(investmentRepository.findAllByUserId(userId)).thenReturn(List.of(investment));
        when(cashFlowService.extractCashFlows(any())).thenReturn(Collections.singletonList(BigDecimal.ZERO)); // Mock cash flow extraction

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
        investment.setCurrentQuantity(5);

        InvestmentResponse response = investmentService.createInvestmentResponse(investment);

        assertEquals(1L, response.getId());
        assertEquals("Investment A", response.getName());
        assertEquals(BigDecimal.valueOf(50), response.getTotalValue());
    }

    @Test
    public void calculateTotalValue_ValidInvestment_ReturnsCorrectTotalValue() {
        Investment investment = new Investment();
        investment.setCurrentPrice(BigDecimal.valueOf(10));
        investment.setCurrentQuantity(5);

        BigDecimal totalValue = investmentService.calculateTotalValue(investment);

        assertEquals(BigDecimal.valueOf(50), totalValue);
    }
}

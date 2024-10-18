package com.example.generator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;

import com.example.model.Dividend;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.repository.DividendRepository;
import com.example.repository.InvestmentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DividendGeneratorTest {

    @InjectMocks
    private DividendGenerator dividendGenerator;

    @Mock
    private DividendRepository dividendRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void generateDividends_forFundInvestment_createsFourDividends() {
        Investment fundInvestment = createMockInvestment("Fund A", 100, 10);
        dividendGenerator.generateDividends(fundInvestment);
        verify(dividendRepository, times(4)).save(any(Dividend.class)); // 4 dividends for fund
    }

    @Test
    public void generateDividends_forCompanyInvestment_createsOneDividend() {
        Investment companyInvestment = createMockInvestment("Company A", 100, 10);
        dividendGenerator.generateDividends(companyInvestment);
        verify(dividendRepository, times(1)).save(any(Dividend.class)); // 1 dividend for company
    }

    @Test
    public void generateDividends_forEmptyInvestment_doesNotCreateDividends() {
        Investment emptyInvestment = new Investment();
        emptyInvestment.setName("Empty Investment");
        emptyInvestment.setCurrentPrice(BigDecimal.valueOf(100));
        emptyInvestment.setUserId(1L);
        emptyInvestment.setTransactions(Collections.emptyList()); // No transactions

        dividendGenerator.generateDividends(emptyInvestment);
        verify(dividendRepository, never()).save(any(Dividend.class)); // No dividends should be generated
    }

    private Investment createMockInvestment(String name, double price, int quantity) {
        Investment investment = new Investment();
        investment.setName(name);
        investment.setCurrentPrice(BigDecimal.valueOf(price));
        investment.setUserId(1L);
        investment.setCurrentQuantity(quantity);
        
        // Create a mock transaction
        Transaction transaction = new Transaction();
        transaction.setTimestamp(Instant.now());
        investment.setTransactions(Collections.singletonList(transaction)); // Simulating one transaction
        
        return investment;
    }

    // TODO test imestamp logic

    // TODO test dividend logic
}

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
import com.example.service.TransactionService;

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
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Investment createMockInvestment(String name, double price, int quantity) {
        if (price < 0) {
            price = 0; 
        }
        Investment investment = new Investment();
        investment.setName(name);
        investment.setCurrentPrice(BigDecimal.valueOf(price));
        investment.setUserId(1L);
        
        Transaction transaction = new Transaction();
        transaction.setTimestamp(Instant.now());
        transaction.setQuantity(quantity); 
        investment.setTransactions(Collections.singletonList(transaction)); 
        
        return investment;
    }

    @Test
    public void generateDividends_ForFundInvestment_CreatesFourDividends() {
        Investment fundInvestment = createMockInvestment("Fund A", 100, 10);
        dividendGenerator.generateDividends(fundInvestment);
        verify(dividendRepository, times(4)).save(any(Dividend.class)); // 4 dividends for fund
    }

    @Test
    public void generateDividends_ForCompanyInvestment_CreatesOneDividend() {
        Investment companyInvestment = createMockInvestment("Company A", 100, 10);
        dividendGenerator.generateDividends(companyInvestment);
        verify(dividendRepository, times(1)).save(any(Dividend.class)); // 1 dividend for company
    }

    @Test
    public void generateDividends_ForEmptyInvestment_DoesNotCreateDividends() {
        Investment emptyInvestment = new Investment();
        emptyInvestment.setName("Empty Investment");
        emptyInvestment.setCurrentPrice(BigDecimal.valueOf(100));
        emptyInvestment.setUserId(1L);
        emptyInvestment.setTransactions(Collections.emptyList()); 
        
        dividendGenerator.generateDividends(emptyInvestment);
        
        verify(dividendRepository, never()).save(any(Dividend.class));
    }

    @Test
    public void generateDividends_WithZeroQuantityTransaction_DoesNotCreateDividends() {
        Investment investment = createMockInvestment("Fund A", 100, 0); 

        dividendGenerator.generateDividends(investment);

        verify(dividendRepository, never()).save(any(Dividend.class)); 
    }

    @Test
    public void generateDividends_WithZeroInvestmentPrice_DoesNotCreateDividends() {
        Investment investment = createMockInvestment("Fund A", 0, 10); 

        dividendGenerator.generateDividends(investment);

        verify(dividendRepository, never()).save(any(Dividend.class)); 
    }

    @Test
    public void generateDividends_WithNullAttributes_DoesNotCreateDividends() {
        Investment investment = new Investment();
        investment.setName("Fund A");
        investment.setCurrentPrice(BigDecimal.ZERO); 
        investment.setTransactions(null); 

        dividendGenerator.generateDividends(investment); 

        verify(dividendRepository, never()).save(any(Dividend.class));
    }
}

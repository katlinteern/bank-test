package com.example.generator;

import com.example.model.Investment;
import com.example.repository.InvestmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InvestmentGeneratorTest {

    @InjectMocks
    private InvestmentGenerator investmentGenerator;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private TransactionGenerator transactionGenerator;

    @Mock
    private DividendGenerator dividendGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Tests for generateInvestmentData
    @Test
    void generateInvestmentData_SavesInvestmentsAndGeneratesTransactionsAndDividends() {
        
        when(transactionGenerator.generateTransactions(any())).thenReturn(Collections.emptyList());

        investmentGenerator.generateInvestmentData();

        verify(investmentRepository, times(10)).save(any(Investment.class));
        verify(transactionGenerator, times(10)).generateTransactions(any());
        verify(dividendGenerator, times(10)).generateDividends(any());
    }

    // Tests for createInvestment
    @Test
    void createInvestment_ValidName_CreatesInvestment() {
        Investment investment = investmentGenerator.createInvestment("Valid Investment");

        assertNotNull(investment);
        assertEquals("Valid Investment", investment.getName());
        assertNotNull(investment.getCurrentPrice());
        assertEquals(Long.valueOf(1), investment.getUserId());
    }

    @Test
    void createInvestment_NullName_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            investmentGenerator.createInvestment(null);
        });
        assertEquals("Investment name cannot be null or empty.", exception.getMessage());
    }

    @Test
    void createInvestment_EmptyName_ThrowsIllegalArgumentException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            investmentGenerator.createInvestment("  ");
        });
        assertEquals("Investment name cannot be null or empty.", exception.getMessage());
    }

    @Test
    void createInvestment_SetsRandomPriceWithinExpectedRange() {
        Investment investment = investmentGenerator.createInvestment("Test Investment");

        BigDecimal price = investment.getCurrentPrice();
        assertNotNull(price);
        assertTrue(price.compareTo(BigDecimal.valueOf(60)) >= 0);
        assertTrue(price.compareTo(BigDecimal.valueOf(160)) < 0);
    }
}

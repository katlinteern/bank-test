package com.example.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.model.Investment;
import com.example.repository.DividendRepository;
import com.example.repository.InvestmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;

public class DataGeneratorTest {

    @InjectMocks
    private DataGenerator dataGenerator;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private TransactionGenerator transactionGenerator;

    @Mock
    private DividendGenerator dividendGenerator;

    @Mock
    private DividendRepository dividendRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createInvestment_returnsInvestmentWithValidPriceRange() {
        Investment investment = dataGenerator.createInvestment("Test Investment");

        assertEquals("Test Investment", investment.getName());
        assertEquals(1L, investment.getUserId());
        assertTrue(investment.getCurrentPrice().compareTo(BigDecimal.valueOf(50)) >= 0);
        assertTrue(investment.getCurrentPrice().compareTo(BigDecimal.valueOf(200)) <= 0);
    }

    @Test
    public void generateInvestmentData_savesInvestmentsAndCallsGenerators() {
        when(transactionGenerator.generateTransactions(any(Investment.class))).thenReturn(Collections.emptyList());

        dataGenerator.generateInvestmentData();

        verify(investmentRepository, times(10)).save(any(Investment.class));
        verify(transactionGenerator, times(10)).generateTransactions(any(Investment.class));
        verify(dividendGenerator, times(10)).generateDividends(any(Investment.class));
    }

    @Test
    public void generateInvestmentData_handlesEmptyTransactions() {
        when(transactionGenerator.generateTransactions(any(Investment.class))).thenReturn(Collections.emptyList());
        dataGenerator.generateInvestmentData();

        verify(investmentRepository, times(10)).save(any(Investment.class));
    }
}

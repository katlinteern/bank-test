package com.example.generator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.model.Investment;
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

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createInvestment_withEmptyName_shouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dataGenerator.createInvestment("");
        });

        String expectedMessage = "Investment name cannot be null or empty.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Expected exception message should indicate name is invalid");
    }
    
    @Test
    public void createInvestment_shouldHavePriceGreaterThanZero() {
        Investment investment = dataGenerator.createInvestment("Valid Investment");

        assertTrue(investment.getCurrentPrice().compareTo(BigDecimal.ZERO) > 0, 
                   "Investment price should be greater than zero.");
    }

    @Test
    public void createInvestment_shouldNotExceedMaximumPrice() {
        Investment investment = dataGenerator.createInvestment("Test Investment");

        assertTrue(investment.getCurrentPrice().compareTo(BigDecimal.valueOf(200)) <= 0, 
                   "Investment price should not exceed 200");
    }

    @Test
    public void createInvestment_shouldHavePositiveUserId() {
        Investment investment = dataGenerator.createInvestment("Test Investment");

        assertTrue(investment.getUserId() >= 1, "User ID should be greater than or equal to 1");
    }

    @Test
    public void generateInvestmentData_savesUniqueInvestments() {
        dataGenerator.generateInvestmentData();

        verify(investmentRepository, times(10)).save(any(Investment.class));
    }

    @Test
    public void generateInvestmentData_checksTransactionGeneration() {
        when(transactionGenerator.generateTransactions(any(Investment.class))).thenReturn(Collections.emptyList());

        dataGenerator.generateInvestmentData();

        verify(transactionGenerator, times(10)).generateTransactions(any(Investment.class));
    }

    @Test
    public void generateInvestmentData_checksDividendGeneration() {
        dataGenerator.generateInvestmentData();

        verify(dividendGenerator, times(10)).generateDividends(any(Investment.class));
    }
}

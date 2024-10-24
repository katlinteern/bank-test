package com.example.generator;

import static org.mockito.Mockito.*;

import com.example.repository.InvestmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DataGeneratorTest {

    @InjectMocks
    private DataGenerator dataGenerator;

    @Mock
    private InvestmentGenerator investmentGenerator; 

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
    public void run_shouldCallGenerateInvestmentData() {
        dataGenerator.run();

        verify(investmentGenerator, times(1)).generateInvestmentData();
    }
}

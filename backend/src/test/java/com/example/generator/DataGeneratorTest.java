package com.example.generator;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.example.enums.TransactionType;
import com.example.model.Dividend;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.repository.DividendRepository;
import com.example.repository.InvestmentRepository;
import com.example.repository.TransactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class DataGeneratorTest {
/* 
    @InjectMocks
    private DataGenerator dataGenerator;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private DividendRepository dividendRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void generateInvestmentData_whenCalled_createsInvestments() {
        Investment mockInvestment = new Investment();
        mockInvestment.setName("Fund A");
        mockInvestment.setCurrentPrice(BigDecimal.valueOf(100));
        mockInvestment.setUserId(1L);

        when(investmentRepository.save(any(Investment.class))).thenReturn(mockInvestment);

        dataGenerator.generateInvestmentData();

        verify(investmentRepository, times(10)).save(any(Investment.class)); // Check if 10 investments are created
    }

    @Test
    public void generateInvestmentData_whenCalled_createsTransactions() {
        Investment mockInvestment = new Investment();
        mockInvestment.setName("Fund A");
        mockInvestment.setCurrentPrice(BigDecimal.valueOf(100));
        mockInvestment.setUserId(1L);

        when(investmentRepository.save(any(Investment.class))).thenReturn(mockInvestment);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        dataGenerator.generateInvestmentData();

        // Ensure transactions were generated and saved
        verify(transactionRepository, atLeastOnce()).save(any(Transaction.class));
    }

    @Test
    public void generateInvestmentData_whenCalled_createsDividends() {
        Investment mockInvestment = new Investment();
        mockInvestment.setName("Fund A");
        mockInvestment.setCurrentPrice(BigDecimal.valueOf(100));
        mockInvestment.setUserId(1L);
        mockInvestment.setTransactions(new ArrayList<>()); // Assuming transactions are generated

        when(investmentRepository.save(any(Investment.class))).thenReturn(mockInvestment);
        doNothing().when(dividendRepository).save(any(Dividend.class));

        dataGenerator.generateInvestmentData();

        // Check if dividends are created (assume there are dividends for each investment)
        verify(dividendRepository, times(1)).save(any(Dividend.class));
    }

    @Test
    public void calculateCurrentQuantity_whenCalled_calculatesCorrectly() {
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(createTransaction(TransactionType.BUY, 10));
        transactions.add(createTransaction(TransactionType.SELL, 5));

        int currentQuantity = dataGenerator.calculateCurrentQuantity(transactions);

        // Expected remaining quantity should be 5 (10 bought - 5 sold)
        assertTrue(currentQuantity == 5, "Current quantity should be 5.");
    }

    private Transaction createTransaction(TransactionType type, int quantity) {
        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setQuantity(quantity);
        return transaction;
    } */
}

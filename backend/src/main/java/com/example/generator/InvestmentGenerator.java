package com.example.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.repository.InvestmentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
public class InvestmentGenerator {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private TransactionGenerator transactionGenerator;

    @Autowired
    private DividendGenerator dividendGenerator;

    public void generateInvestmentData() {
        String[] investmentNames = {
            "Fund A", "Fund B", "Fund C", 
            "Company A", "Company B", "Company C", 
            "Fund D", "Fund E", 
            "Company D", "Company E"
        };
        
        for (String investmentName : investmentNames) {
            Investment investment = createInvestment(investmentName);
            investmentRepository.save(investment);

            List<Transaction> transactions = transactionGenerator.generateTransactions(investment);
            if (!transactions.isEmpty()) {
                investment.setTransactions(transactions);
            }

            dividendGenerator.generateDividends(investment);
        }
    }

    public Investment createInvestment(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Investment name cannot be null or empty.");
        }
    
        Investment investment = new Investment();
        investment.setName(name);
        
        BigDecimal randomPrice = BigDecimal.valueOf(50 + new Random().nextInt(100));
        investment.setCurrentPrice(randomPrice);
        investment.setUserId(1L);
        return investment;
    }
    
}

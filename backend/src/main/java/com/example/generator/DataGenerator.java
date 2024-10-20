package com.example.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.repository.InvestmentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

@Component
public class DataGenerator implements CommandLineRunner {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private TransactionGenerator transactionGenerator;

    @Autowired
    private DividendGenerator dividendGenerator;

    @Override
    public void run(String... args) {
        generateInvestmentData();
    }

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
            investment.setTransactions(transactions);

            dividendGenerator.generateDividends(investment);
        }
    }

    public Investment createInvestment(String name) {
        Investment investment = new Investment();
        investment.setName(name);
        investment.setCurrentPrice(BigDecimal.valueOf(50 + new Random().nextInt(150))); // Price between 50 and 200
        investment.setUserId(1L); // Simplified user ID
        return investment;
    }
}

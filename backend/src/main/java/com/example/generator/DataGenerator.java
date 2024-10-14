package com.example.generator;

import com.example.enums.TransactionType;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.model.Dividend;
import com.example.repository.InvestmentRepository;
import com.example.repository.TransactionRepository;
import com.example.repository.DividendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class DataGenerator implements CommandLineRunner {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DividendRepository dividendRepository;

    @Override
    public void run(String... args) {
        generateData();
    }

    private void generateData() {
        Long userId = 1L;  

        for (int i = 1; i <= 10; i++) {
            String investmentName;
            if (i % 2 == 0) {
                investmentName = "Fund " + i;  
            } else {
                investmentName = "Company " + i; 
            }
            Investment investment = createInvestment(userId, investmentName);

            generateRandomTransactions(investment);
            generateRandomDividends(investment);
        }
    }

    private Investment createInvestment(Long userId, String name) {
        Investment investment = new Investment();
        investment.setUserId(userId);
        investment.setName(name);
        return investmentRepository.save(investment);
    }

    private void generateRandomTransactions(Investment investment) {
        Random random = new Random();

        for (int i = 0; i < 100; i++) {  
            Transaction transaction = new Transaction();

            TransactionType[] types = TransactionType.values();
            TransactionType randomType = types[random.nextInt(types.length)];

            transaction.setType(randomType);
            transaction.setQuantity(random.nextInt(1, 100));
            transaction.setPrice(BigDecimal.valueOf(random.nextDouble(20.0, 200.0)));
            transaction.setFee(BigDecimal.valueOf(random.nextDouble(0.5, 5.0)));
            transaction.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(1, 365)));
            transaction.setInvestment(investment);
            transactionRepository.save(transaction);
        }
    }

    private void generateRandomDividends(Investment investment) {
        Random random = new Random();
        String investmentName = investment.getName();

        if (investmentName.startsWith("Company")) {
            Dividend dividend = new Dividend();
            dividend.setAmount(BigDecimal.valueOf(random.nextDouble(1.0, 10.0)));  
            dividend.setInvestment(investment);
            dividend.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(1, 365)));  
            dividendRepository.save(dividend);
        } else if (investmentName.startsWith("Fund")) {
            for (int i = 0; i < 4; i++) {
                Dividend dividend = new Dividend();
                dividend.setAmount(BigDecimal.valueOf(random.nextDouble(0.5, 5.0)));  
                dividend.setInvestment(investment);
                dividend.setTimestamp(LocalDateTime.now().minusDays(random.nextInt(1, 365))); 
                dividendRepository.save(dividend);
            }
        }
    }
}

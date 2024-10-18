package com.example.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.enums.TransactionType;
import com.example.model.Dividend;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.repository.DividendRepository;
import com.example.repository.InvestmentRepository;
import com.example.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataGenerator implements CommandLineRunner {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DividendRepository dividendRepository;

    private final Random random = new Random();
    private static final String[] INVESTMENT_NAMES = {"Fund A", "Fund B", "Fund C", "Company A", "Company B", "Company C", "Fund D", "Fund E", "Company D", "Company E"};
    
    @Override
    public void run(String... args) {
        generateData();
    }

    private void generateData() {
        for (String investmentName : INVESTMENT_NAMES) {
            Investment investment = new Investment();
            investment.setName(investmentName);
            investment.setCurrentPrice(BigDecimal.valueOf(50 + random.nextInt(50))); // Set current price between 50 and 100
            investment.setUserId(1L); // Assign a user ID for simplicity

            // Save the investment before generating transactions
            investmentRepository.save(investment);
            
            // Generate transactions for the saved investment
            List<Transaction> transactions = generateTransactions(investment);
            investment.setCurrentQuantity(calculateCurrentQuantity(transactions));
            investment.setTransactions(transactions);

            // Update the investment in the database after associating transactions
            investmentRepository.save(investment);
            
            generateDividends(investment);
        }
    }

    public List<Transaction> generateTransactions(Investment investment) {
        List<Transaction> transactions = new ArrayList<>();
        Instant timestamp = Instant.now().minus(6 * 365, ChronoUnit.DAYS); // 6-year horizon
        int totalQuantity = 0; // To keep track of total buy quantity
    
        for (int i = 0; i < 100; i++) {
            Transaction transaction = new Transaction();
            transaction.setInvestment(investment);
    
            // Randomly decide the interval: between 1 - 3 weeks
            long randomDays = 7 + random.nextInt(14); // Random interval between 1 - 3 weeks
            timestamp = timestamp.plus(randomDays, ChronoUnit.DAYS); // Move the timestamp forward by random days
            transaction.setTimestamp(timestamp); // Ensure transactions are in chronological order
            
            // Determine transaction type
            if (random.nextBoolean() && totalQuantity > 1) { // 50% chance to sell if we have at least 2 (to leave something remaining)
                int maxSellQuantity = totalQuantity / 2; // Limit sell to 50% of total quantity
                int sellQuantity = random.nextInt(maxSellQuantity) + 1; // Random sell quantity from 1 to maxSellQuantity
                transaction.setType(TransactionType.SELL);
                transaction.setQuantity(sellQuantity);
                totalQuantity -= sellQuantity; // Update the total quantity
            } else {
                transaction.setType(TransactionType.BUY);
                int buyQuantity = random.nextInt(5) + 1; // Random buy quantity between 1 and 5
                transaction.setQuantity(buyQuantity);
                totalQuantity += buyQuantity; // Update the total quantity
            }
            
            // Set price with ±5 fluctuation
            BigDecimal priceFluctuation = BigDecimal.valueOf(random.nextInt(11) - 5); // Random fluctuation between -5 and +5
            transaction.setPrice(investment.getCurrentPrice().add(priceFluctuation));
            
            // Set transaction fee
            transaction.setFee(BigDecimal.valueOf(random.nextDouble() * 10)); // Random fee between 0 and 10
            
            transactions.add(transaction);
            transactionRepository.save(transaction);
        }
        return transactions;
    }
    

    private int calculateCurrentQuantity(List<Transaction> transactions) {
        int totalBuy = 0;
        int totalSell = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.BUY) {
                totalBuy += transaction.getQuantity();
            } else {
                totalSell += transaction.getQuantity();
            }
        }
        return totalBuy - totalSell; // Remaining quantity
    }

    private void generateDividends(Investment investment) {
        Instant dividendStart = investment.getTransactions().get(0).getTimestamp(); // Start after first transaction
        for (int i = 0; i < (investment.getName().startsWith("Fund") ? 4 : 1); i++) {
            Dividend dividend = new Dividend();
            dividend.setInvestment(investment);
            dividend.setTimestamp(dividendStart.plus(i * 90, ChronoUnit.DAYS)); // For Funds, 4 dividends in a year
            
            BigDecimal totalValue = investment.getCurrentPrice().multiply(BigDecimal.valueOf(investment.getCurrentQuantity()));
            BigDecimal dividendAmount = totalValue.multiply(BigDecimal.valueOf(0.02)); // Assuming a fixed 2% dividend
            
            dividend.setAmount(dividendAmount);
            dividendRepository.save(dividend);
        }
    }
}

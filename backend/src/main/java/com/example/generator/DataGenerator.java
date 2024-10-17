package com.example.generator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

@Component
public class DataGenerator implements CommandLineRunner {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private DividendRepository dividendRepository;

    private final Random random = new Random();
    private static final double MIN_PRICE = 20.0;
    private static final double MAX_PRICE = 200.0;
    private static final double MIN_FEE = 0.5;
    private static final double MAX_FEE = 5.0;

    @Override
    public void run(String... args) {
        generateData();
    }

    private void generateData() {
        Long userId = 1L;

        for (int i = 1; i <= 10; i++) {
            String investmentName = (i % 2 == 0) ? "Fund " + i : "Company " + i;
            Investment investment = createInvestment(userId, investmentName);

            // Generate initial purchase transactions
            int initialQuantity = random.nextInt(50, 101); // Initial quantity between 50 and 100
            BigDecimal purchasePrice = BigDecimal.valueOf(random.nextDouble(MIN_PRICE, MAX_PRICE));
            generatePurchaseTransaction(investment, initialQuantity, purchasePrice);

            // Generate subsequent sales transactions based on the initial purchase
            generateRandomSalesTransactions(investment, initialQuantity, purchasePrice);
            generateRandomDividends(investment, investmentName.startsWith("Company") ? 1 : 4);
        }
    }

    private Investment createInvestment(Long userId, String name) {
        Investment investment = new Investment();
        investment.setUserId(userId);
        investment.setName(name);
        return investmentRepository.save(investment);
    }

    private void generatePurchaseTransaction(Investment investment, int quantity, BigDecimal price) {
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(quantity);
        transaction.setPrice(price);
        transaction.setFee(BigDecimal.valueOf(random.nextDouble(MIN_FEE, MAX_FEE)));
        transaction.setTimestamp(Instant.now().minus(Period.ofDays(random.nextInt(1, 730)))); // Up to 2 years ago
        transaction.setInvestment(investment);
        transactionRepository.save(transaction);
    }

    private void generateRandomSalesTransactions(Investment investment, int initialQuantity, BigDecimal purchasePrice) {
        List<Integer> saleQuantities = new ArrayList<>();
        int maxSales = random.nextInt(5, 10); // Random number of sales transactions between 5 and 10
        int totalSold = 0;

        for (int i = 0; i < maxSales; i++) {
            int remainingQuantity = initialQuantity - totalSold;

            // If there's no remaining quantity to sell, break out of the loop
            if (remainingQuantity <= 0) {
                break;
            }

            // Ensure valid bounds for the nextInt call
            int saleQuantity = random.nextInt(1, Math.max(remainingQuantity / 2 + 1, 2)); // Ensure at least 2
            totalSold += saleQuantity;
            saleQuantities.add(saleQuantity);
        }

        // Sort sales quantities to simulate time-based ordering
        Collections.sort(saleQuantities);

        Instant currentTimestamp = Instant.now();

        for (int quantity : saleQuantities) {
            Transaction transaction = new Transaction();
            BigDecimal salePrice = purchasePrice.add(BigDecimal.valueOf(random.nextDouble(-5.0, 5.0))); // Slight variation in sale price
            transaction.setType(TransactionType.SELL);
            transaction.setQuantity(quantity);
            transaction.setPrice(salePrice);
            transaction.setFee(BigDecimal.valueOf(random.nextDouble(MIN_FEE, MAX_FEE)));
            transaction.setTimestamp(currentTimestamp.minus(Period.ofDays(random.nextInt(1, 730)))); // Random time over 2 years
            transaction.setInvestment(investment);
            transactionRepository.save(transaction);
            currentTimestamp = transaction.getTimestamp(); // Ensure chronological order
        }
    }

    private void generateRandomDividends(Investment investment, int count) {
        String investmentName = investment.getName();

        for (int i = 0; i < count; i++) {
            Dividend dividend = new Dividend();
            double minDividend = investmentName.startsWith("Company") ? 1.0 : 0.5;
            double maxDividend = investmentName.startsWith("Company") ? 10.0 : 5.0;
            dividend.setAmount(BigDecimal.valueOf(random.nextDouble(minDividend, maxDividend))); // Ensure valid range
            dividend.setInvestment(investment);
            dividend.setTimestamp(Instant.now().minus(Period.ofDays(random.nextInt(1, 730)))); // Random time over 2 years
            dividendRepository.save(dividend);
        }
    }
}

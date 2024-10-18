package com.example.generator;

import com.example.enums.TransactionType;
import com.example.model.Transaction;
import com.example.model.Investment;
import com.example.repository.TransactionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class TransactionGenerator {

    @Autowired
    private TransactionRepository transactionRepository;

    private final Random random = new Random();

    public List<Transaction> generateTransactions(Investment investment) {
        List<Transaction> transactions = new ArrayList<>();
        Instant timestamp = Instant.now().minus(6 * 365, ChronoUnit.DAYS); // 6-year horizon
        int totalQuantity = 0;
        BigDecimal purchasePrice = investment.getCurrentPrice();
    
        for (int i = 0; i < 100; i++) {
            Transaction transaction = createTransaction(investment, timestamp, purchasePrice, totalQuantity);
            transactions.add(transaction);
            transactionRepository.save(transaction);
            timestamp = transaction.getTimestamp(); // Update timestamp for next transaction
        }
        return transactions;
    }

    private Transaction createTransaction(Investment investment, Instant timestamp, BigDecimal purchasePrice, int totalQuantity) {
        Transaction transaction = new Transaction();
        transaction.setInvestment(investment);

        // Random interval: between 1 - 3 weeks
        long randomDays = 7 + random.nextInt(14);
        timestamp = timestamp.plus(randomDays, ChronoUnit.DAYS);
        transaction.setTimestamp(timestamp);
        
        BigDecimal priceFluctuation = BigDecimal.valueOf(random.nextInt(21) - 10); // +- 10
        BigDecimal currentPrice = purchasePrice.add(priceFluctuation);
        transaction.setPrice(currentPrice);

        // Determine transaction type
        if (shouldSell(totalQuantity, currentPrice, purchasePrice)) {
            int sellQuantity = determineSellQuantity(totalQuantity);
            if (sellQuantity > 0) {
                transaction.setType(TransactionType.SELL);
                transaction.setQuantity(sellQuantity);
                totalQuantity -= sellQuantity;
            }
        } else {
            int buyQuantity = random.nextInt(21) + 10; // Buy between 10 and 30
            transaction.setType(TransactionType.BUY);
            transaction.setQuantity(buyQuantity);
            totalQuantity += transaction.getQuantity();
        }

        transaction.setFee(BigDecimal.valueOf(random.nextDouble() * 10)); // Fee between 0 and 10
        
        return transaction;
    }

    private boolean shouldSell(int totalQuantity, BigDecimal currentPrice, BigDecimal purchasePrice) {
        return random.nextBoolean() && totalQuantity > 1 && currentPrice.compareTo(purchasePrice) > 0;
    }

    private int determineSellQuantity(int totalQuantity) {
        return totalQuantity / 2 > 0 ? random.nextInt(totalQuantity / 2) + 1 : 0; // Sell up to 50% of total
    }

}

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

    private static final int TOTAL_TRANSACTIONS = 100;
    private static final int MINIMUM_DAYS_INTERVAL = 7;
    private static final int MAXIMUM_DAYS_INTERVAL = 21;
    private static final int PRICE_FLUCTUATION_BOUND = 10; // Price can vary +-10
    private static final int MINIMUM_BUY_QUANTITY = 10;
    private static final int MAXIMUM_BUY_QUANTITY = 30;
    private static final double MAXIMUM_FEE = 10.0; // Transaction fee can vary between 0 and 10
    private static long INVESTMENT_HORIZON_DAYS = 365 * 6; // Default to 6 years in days
    private static BigDecimal MINIMUM_PRICE = new BigDecimal("1");

    @Autowired
    private TransactionRepository transactionRepository;

    private final Random random = new Random();

    public List<Transaction> generateTransactions(Investment investment) {
        BigDecimal initialPrice = investment.getCurrentPrice();
        
        if (initialPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Investment price must be greater than zero.");
        }

        List<Transaction> transactions = new ArrayList<>();
        Instant initialTimestamp = Instant.now().minus(INVESTMENT_HORIZON_DAYS, ChronoUnit.DAYS); 
        BigDecimal price = initialPrice;
        int totalQuantity = 0;

        for (int i = 0; i < TOTAL_TRANSACTIONS; i++) {
            Instant transactionTimestamp = getNextTimestamp(initialTimestamp);
            Transaction transaction = createTransaction(investment, transactionTimestamp, price, totalQuantity);
            transactions.add(transaction);
            transactionRepository.save(transaction);
            totalQuantity = updateQuantity(transaction, totalQuantity);
            initialTimestamp = transactionTimestamp;
        }
        return transactions;
    }

    private Instant getNextTimestamp(Instant currentTimestamp) {
        long randomDays = MINIMUM_DAYS_INTERVAL + random.nextInt(MAXIMUM_DAYS_INTERVAL - MINIMUM_DAYS_INTERVAL + 1);
        return currentTimestamp.plus(randomDays, ChronoUnit.DAYS);
    }

    private Transaction createTransaction(Investment investment, Instant timestamp, BigDecimal price, int totalQuantity) {
        Transaction transaction = new Transaction();
        transaction.setInvestment(investment);
        transaction.setTimestamp(timestamp);
        
        BigDecimal currentPrice = price.add(getPriceFluctuation());
        
        // Ensure the price never goes below zero
        if (currentPrice.compareTo(MINIMUM_PRICE) < 0) {
            currentPrice = MINIMUM_PRICE;
        }
        transaction.setPrice(currentPrice);


        if (isSellTransaction(totalQuantity, currentPrice, price)) {
            handleSellTransaction(transaction, totalQuantity);
        } else {
            handleBuyTransaction(transaction);
        }

        transaction.setFee(getRandomFee());
        return transaction;
    }

    private BigDecimal getPriceFluctuation() {
        return BigDecimal.valueOf(random.nextInt(PRICE_FLUCTUATION_BOUND * 2 + 1) - PRICE_FLUCTUATION_BOUND); // +/- fluctuation
    }

    private boolean isSellTransaction(int totalQuantity, BigDecimal currentPrice, BigDecimal price) {
        return random.nextBoolean() && totalQuantity > 1 && currentPrice.compareTo(price) > 0;
    }

    private void handleSellTransaction(Transaction transaction, int totalQuantity) {
        int sellQuantity = determineSellQuantity(totalQuantity);
        if (sellQuantity > 0) {
            transaction.setType(TransactionType.SELL);
            transaction.setQuantity(sellQuantity);
        }
    }

    private void handleBuyTransaction(Transaction transaction) {
        int buyQuantity = MINIMUM_BUY_QUANTITY + random.nextInt(MAXIMUM_BUY_QUANTITY - MINIMUM_BUY_QUANTITY + 1);
        transaction.setType(TransactionType.BUY);
        transaction.setQuantity(buyQuantity);
    }

    private int determineSellQuantity(int totalQuantity) {
        return totalQuantity / 2 > 0 ? random.nextInt(totalQuantity / 2) + 1 : 0; // Sell up to 50% of total
    }

    private int updateQuantity(Transaction transaction, int totalQuantity) {
        if (transaction.getType() == TransactionType.BUY) {
            return totalQuantity + transaction.getQuantity();
        } else if (transaction.getType() == TransactionType.SELL) {
            return totalQuantity - transaction.getQuantity();
        }
        return totalQuantity;
    }

    private BigDecimal getRandomFee() {
        return BigDecimal.valueOf(random.nextDouble() * MAXIMUM_FEE);
    }
}

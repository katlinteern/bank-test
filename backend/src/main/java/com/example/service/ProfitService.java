package com.example.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.dto.ProfitResult;
import com.example.enums.TransactionType;
import com.example.model.Investment;
import com.example.model.Transaction;

@Service
public class ProfitService {
    private static final Logger logger = LoggerFactory.getLogger(ProfitService.class);

    public ProfitResult calculateInvestmentProfit(Investment investment) {
        if (investment == null || investment.getTransactions() == null) {
            logger.warn("Invalid investment or transactions are null for investment ID: {}", investment != null ? investment.getId() : "unknown");
            return new ProfitResult(BigDecimal.ZERO, BigDecimal.ZERO);
        }

        logger.info("Calculating profit for investment ID: {}", investment.getId());
        
        List<Transaction> transactions = investment.getTransactions();
        List<BigDecimal> cashFlows = getCashFlowsFromTransactions(transactions);
        
        BigDecimal totalProfit = cashFlows.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalValue = cashFlows.stream()
                .filter(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) < 0)
                .map(BigDecimal::abs) // Convert to positive
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ProfitResult(totalProfit, totalValue);
    }

    private List<BigDecimal> getCashFlowsFromTransactions(List<Transaction> transactions) {
        return transactions.stream()
                .map(this::calculateCashFlow)
                .collect(Collectors.toList());
    }

    private BigDecimal calculateCashFlow(Transaction transaction) {
        BigDecimal price = transaction.getPrice();
        BigDecimal quantity = BigDecimal.valueOf(transaction.getQuantity());
        BigDecimal fee = transaction.getFee();
        
        if (transaction.getType() == TransactionType.BUY) {
            return price.multiply(quantity.negate()).subtract(fee); // Negative for investment
        } else { 
            return price.multiply(quantity).subtract(fee); // Positive for income
        }
    }

    public Double calculateProfitPercentage(BigDecimal totalProfit, BigDecimal totalValue) {
        if (totalValue.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0; // Avoid division by zero
        }
        // Profit percentage = (totalProfit / totalValue) * 100
        return totalProfit.divide(totalValue, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}

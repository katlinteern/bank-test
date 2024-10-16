package com.example.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.dto.ProfitabilityResult;
import com.example.enums.TransactionType;
import com.example.model.Investment;
import com.example.model.Transaction;

@Service
public class ProfitabilityService {
    private static final Logger logger = LoggerFactory.getLogger(ProfitabilityService.class);

    public ProfitabilityResult calculateInvestmentProfitability(Investment investment) {
        if (investment == null || investment.getTransactions() == null) {
            logger.warn("Invalid investment or transactions are null for investment ID: {}", investment != null ? investment.getId() : "unknown");
            return new ProfitabilityResult(BigDecimal.ZERO, BigDecimal.ZERO);
        }

        logger.info("Calculating profitability for investment ID: {}", investment.getId());
        
        List<Transaction> transactions = investment.getTransactions();
        List<BigDecimal> cashFlows = getCashFlowsFromTransactions(transactions);
        
        BigDecimal totalProfitability = cashFlows.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalInvestment = cashFlows.stream()
                .filter(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) < 0)
                .map(BigDecimal::abs) // Convert to positive
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ProfitabilityResult(totalProfitability, totalInvestment);
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

    public Double calculateProfitabilityPercentage(BigDecimal totalProfitability, BigDecimal totalInvestment) {
        if (totalInvestment.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0; // Avoid division by zero
        }
        // Profitability percentage = (totalProfitability / totalInvestment) * 100
        return totalProfitability.divide(totalInvestment, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}

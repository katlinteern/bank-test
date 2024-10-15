package com.example.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dto.ProfitabilityResult;
import com.example.enums.TransactionType;
import com.example.model.Investment;
import com.example.model.Transaction;

@Service
public class ProfitabilityService {

    public ProfitabilityResult calculateInvestmentProfitability(Investment investment) {
        List<BigDecimal> cashFlows = calculateCashFlowsForInvestment(investment);

        BigDecimal totalProfitability = calculateTotalProfitability(cashFlows);
        BigDecimal totalInvestment = calculateTotalInvestment(cashFlows);

        return new ProfitabilityResult(totalProfitability, totalInvestment);
    }

    private List<BigDecimal> calculateCashFlowsForInvestment(Investment investment) {
        List<BigDecimal> cashFlows = new ArrayList<>();

        for (Transaction transaction : investment.getTransactions()) {
            BigDecimal cashFlow;
            if (transaction.getType() == TransactionType.BUY) {
                cashFlow = transaction.getPrice().multiply(BigDecimal.valueOf(-transaction.getQuantity()))
                            .subtract(transaction.getFee());
            } else { 
                cashFlow = transaction.getPrice().multiply(BigDecimal.valueOf(transaction.getQuantity()))
                            .subtract(transaction.getFee());
            }
            cashFlows.add(cashFlow);
        }

        return cashFlows;
    }

    private BigDecimal calculateTotalProfitability(List<BigDecimal> cashFlows) {
        return cashFlows.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateTotalInvestment(List<BigDecimal> cashFlows) {
        return cashFlows.stream()
                        .filter(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) < 0)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .abs(); // Convert to positive
    }

    public Double calculateProfitabilityPercentage(BigDecimal totalProfitability, BigDecimal totalInvestment) {
        if (totalInvestment.equals(BigDecimal.ZERO)) {
            return 0.0; // Avoid division by zero
        }
        // Profitability percentage = (totalProfitability / totalInvestment) * 100
        return totalProfitability.divide(totalInvestment, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100))
                                .doubleValue();
    }

    
}

package com.example.service;

import com.example.dto.ProfitResult;
import com.example.enums.TransactionType;
import com.example.model.Investment;
import com.example.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ProfitService {

    public ProfitResult calculateInvestmentProfit(Investment investment) {
        List<BigDecimal> cashFlows = new ArrayList<>();
        List<Instant> cashFlowDates = new ArrayList<>();

        addCashFlowData(investment, cashFlows, cashFlowDates);

        BigDecimal totalValue = cashFlows.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal xirr = calculateXirr(cashFlowDates, cashFlows);
        
        return new ProfitResult(totalValue, xirr);
    }

    private void addCashFlowData(Investment investment, List<BigDecimal> cashFlows, List<Instant> cashFlowDates) {
        investment.getTransactions().forEach(transaction -> {
            cashFlows.add(calculateTransactionCashFlow(transaction));
            cashFlowDates.add(transaction.getTimestamp());
        });

        investment.getDividends().forEach(dividend -> {
            cashFlows.add(dividend.getAmount());
            cashFlowDates.add(dividend.getTimestamp());
        });
    }

    private BigDecimal calculateTransactionCashFlow(Transaction transaction) {
        BigDecimal price = transaction.getPrice();
        BigDecimal quantity = BigDecimal.valueOf(transaction.getQuantity());
        BigDecimal fee = transaction.getFee();

        return transaction.getType() == TransactionType.BUY
            ? price.multiply(quantity.negate()).subtract(fee)
            : price.multiply(quantity).subtract(fee);
    }

    private BigDecimal calculateXirr(List<Instant> dates, List<BigDecimal> cashFlows) {
        // Implement XIRR calculation logic here
        return BigDecimal.ZERO; // Placeholder for actual XIRR calculation
    }
}

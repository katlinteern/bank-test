package com.example.service;

import com.example.dto.CashFlowData;
import com.example.enums.TransactionType;
import com.example.model.Investment;
import com.example.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class InvestmentProfitService {


    public CashFlowData collectCashFlowData(Investment investment) {
        List<BigDecimal> cashFlows = new ArrayList<>();
        List<Instant> cashFlowDates = new ArrayList<>();

        collectTransactionCashFlows(investment, cashFlows, cashFlowDates);
        collectDividendCashFlows(investment, cashFlows, cashFlowDates);

        return new CashFlowData(cashFlows, cashFlowDates);
    }

    private void collectTransactionCashFlows(Investment investment, List<BigDecimal> cashFlows, List<Instant> cashFlowDates) {
        investment.getTransactions().forEach(transaction -> {
            cashFlows.add(calculateTransactionCashFlow(transaction));
            cashFlowDates.add(transaction.getTimestamp());
        });
    }

    private void collectDividendCashFlows(Investment investment, List<BigDecimal> cashFlows, List<Instant> cashFlowDates) {
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
}

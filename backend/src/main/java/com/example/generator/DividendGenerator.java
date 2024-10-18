package com.example.generator;

import com.example.model.Dividend;
import com.example.model.Investment;
import com.example.repository.DividendRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class DividendGenerator {

    @Autowired
    private DividendRepository dividendRepository;

    public void generateDividends(Investment investment) {
        if (investment.getTransactions() == null || investment.getTransactions().isEmpty()) {
            return; // No transactions, no dividends
        }

        Instant dividendStart = investment.getTransactions().get(0).getTimestamp(); // Start after the first transaction
        int dividendCount = investment.getName().startsWith("Fund") ? 4 : 1; // 4 dividends for funds, 1 for others
        
        for (int i = 0; i < dividendCount; i++) {
            Dividend dividend = createDividend(investment, dividendStart, i);
            dividendRepository.save(dividend);
        }
    }

    private Dividend createDividend(Investment investment, Instant start, int index) {
        Dividend dividend = new Dividend();
        dividend.setInvestment(investment);
        dividend.setTimestamp(start.plus(index * 90, ChronoUnit.DAYS)); // Every 90 days
        
        BigDecimal totalValue = investment.getCurrentPrice().multiply(BigDecimal.valueOf(investment.getCurrentQuantity()));
        BigDecimal dividendAmount = totalValue.multiply(BigDecimal.valueOf(0.04)); // 4% dividend
        dividend.setAmount(dividendAmount);
        
        return dividend;
    }
}

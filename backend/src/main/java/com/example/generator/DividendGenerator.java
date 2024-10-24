package com.example.generator;

import com.example.model.Dividend;
import com.example.model.Investment;
import com.example.model.Transaction;
import com.example.repository.DividendRepository;
import com.example.service.TransactionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class DividendGenerator {

    private static final BigDecimal DIVIDEND_RATE = BigDecimal.valueOf(0.04); 
    private static final int DIVIDEND_INTERVAL_DAYS = 90; 
    private static final int FUND_DIVIDEND_COUNT = 4; 
    private static final int COMPANY_DIVIDEND_COUNT = 1;

    @Autowired
    private DividendRepository dividendRepository;

    @Autowired
    private TransactionService transactionService;

    public void generateDividends(Investment investment) {
        if (isEligibleForDividends(investment)) {
            createAndSaveDividends(investment);
        }
    }

    private boolean isEligibleForDividends(Investment investment) {
        return investment != null &&
               investment.getCurrentPrice() != null &&
               investment.getCurrentPrice().compareTo(BigDecimal.ZERO) > 0 &&
               hasPositiveTransactions(investment.getTransactions());
    }

    private boolean hasPositiveTransactions(List<Transaction> transactions) {
        return transactions != null && 
               !transactions.isEmpty() && 
               transactions.stream().allMatch(transaction -> transaction.getQuantity() > 0);
    }

    private void createAndSaveDividends(Investment investment) {
        Instant dividendStart = getDividendStart(investment);
        int dividendCount = determineDividendCount(investment);
        
        for (int i = 0; i < dividendCount; i++) {
            Dividend dividend = createDividend(investment, dividendStart, i);
            dividendRepository.save(dividend);
        }
    }

    private Instant getDividendStart(Investment investment) {
        return investment.getTransactions().get(0).getTimestamp();
    }

    private int determineDividendCount(Investment investment) {
        return investment.getName().startsWith("Fund") ? FUND_DIVIDEND_COUNT : COMPANY_DIVIDEND_COUNT;
    }

    private Dividend createDividend(Investment investment, Instant start, int index) {
        Dividend dividend = new Dividend();
        dividend.setInvestment(investment);
        dividend.setTimestamp(start.plus(index * DIVIDEND_INTERVAL_DAYS, ChronoUnit.DAYS)); // Every 90 days
        dividend.setAmount(calculateDividendAmount(investment));
        return dividend;
    }

    private BigDecimal calculateDividendAmount(Investment investment) {
        BigDecimal totalValue = investment.getCurrentPrice()
            .multiply(BigDecimal.valueOf(transactionService.calculateTotalQuantity(investment.getTransactions())));
        return totalValue.multiply(DIVIDEND_RATE);
    }
}

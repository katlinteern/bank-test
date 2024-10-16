package com.example.dto;

import java.math.BigDecimal;

public class ProfitResult {
    private BigDecimal profit;
    private BigDecimal totalValue;

    public ProfitResult(BigDecimal profit, BigDecimal totalValue) {
        this.profit = profit;
        this.totalValue = totalValue;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }
}

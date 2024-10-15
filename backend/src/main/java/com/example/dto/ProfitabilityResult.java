package com.example.dto;

import java.math.BigDecimal;

public class ProfitabilityResult {
    private BigDecimal profitability;
    private BigDecimal totalInvestment;

    public ProfitabilityResult(BigDecimal profitability, BigDecimal totalInvestment) {
        this.profitability = profitability;
        this.totalInvestment = totalInvestment;
    }

    public BigDecimal getProfitability() {
        return profitability;
    }

    public BigDecimal getTotalInvestment() {
        return totalInvestment;
    }
}

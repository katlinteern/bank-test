package com.example.dto;

import java.math.BigDecimal;

public class ProfitResult {
    private final BigDecimal totalValue;
    private final BigDecimal xirr; // Add xirr field

    public ProfitResult(BigDecimal totalValue, BigDecimal xirr) {
        this.totalValue = totalValue;
        this.xirr = xirr;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public BigDecimal getXirr() {
        return xirr;
    }
}

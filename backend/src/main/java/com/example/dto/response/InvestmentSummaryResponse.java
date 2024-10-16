package com.example.dto.response;

import java.math.BigDecimal;

public class InvestmentSummaryResponse {

    private BigDecimal totalValue;
    private BigDecimal xirr;
    private int numberOfInvestments;

    public InvestmentSummaryResponse(BigDecimal totalValue, BigDecimal xirr, int numberOfInvestments) {
        this.totalValue = totalValue;
        this.xirr = xirr;
        this.numberOfInvestments = numberOfInvestments;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public BigDecimal getXirr() {
        return xirr;
    }

    public int getNumberOfInvestments() {
        return numberOfInvestments;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public void setXirr(BigDecimal xirr) {
        this.xirr = xirr;
    }

    public void setNumberOfInvestments(int numberOfInvestments) {
        this.numberOfInvestments = numberOfInvestments;
    }
}

package com.example.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Data Transfer Object for Investment summary response
 */
@Data
public class InvestmentSummaryResponse {

    private BigDecimal totalValue;
    private BigDecimal profitability;
    private int numberOfInvestments;

    public InvestmentSummaryResponse() {
    }

    public InvestmentSummaryResponse(BigDecimal totalValue, BigDecimal profitability, int numberOfInvestments) {
        this.totalValue = totalValue;
        this.profitability = profitability;
        this.numberOfInvestments = numberOfInvestments;
    }
}

package com.example.dto.response;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Data Transfer Object for Investment Summary Response
 */
@Data
public class InvestmentSummaryResponse {

    private BigDecimal totalValue; 
    private BigDecimal totalProfit; 
    private Double profitPercentage; 
    private int numberOfInvestments; 

    public InvestmentSummaryResponse(BigDecimal totalValue, BigDecimal totalProfit, 
                                     Double profitPercentage, int numberOfInvestments) {
        this.totalValue = totalValue;
        this.totalProfit = totalProfit;
        this.profitPercentage = profitPercentage;
        this.numberOfInvestments = numberOfInvestments;
    }
}

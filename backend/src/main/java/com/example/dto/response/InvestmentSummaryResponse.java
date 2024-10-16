package com.example.dto.response;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Data Transfer Object for Investment Summary Response
 */
@Data
public class InvestmentSummaryResponse {

    private BigDecimal totalInvestment; 
    private BigDecimal totalProfitability; 
    private Double profitabilityPercentage; 
    private int numberOfInvestments; 

    public InvestmentSummaryResponse(BigDecimal totalInvestment, BigDecimal totalProfitability, 
                                     Double profitabilityPercentage, int numberOfInvestments) {
        this.totalInvestment = totalInvestment;
        this.totalProfitability = totalProfitability;
        this.profitabilityPercentage = profitabilityPercentage;
        this.numberOfInvestments = numberOfInvestments;
    }
}

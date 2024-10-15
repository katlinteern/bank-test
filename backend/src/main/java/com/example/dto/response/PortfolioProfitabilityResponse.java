package com.example.dto.response;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Data Transfer Object for Portfolio profitability response
 */
@Data
public class PortfolioProfitabilityResponse {

    private BigDecimal totalProfitability;
    private Double profitabilityPercentage;

    public PortfolioProfitabilityResponse(BigDecimal totalProfitability, Double profitabilityPercentage) {
        this.totalProfitability = totalProfitability;
        this.profitabilityPercentage = profitabilityPercentage;
    }
}
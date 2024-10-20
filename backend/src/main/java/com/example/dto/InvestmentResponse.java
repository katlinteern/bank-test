package com.example.dto;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Data Transfer Object for Investment response
 */
@Data
public class InvestmentResponse {
    private Long id;
    private String name;
    private BigDecimal totalValue;
    private BigDecimal profitability;

    public InvestmentResponse() {
    }

    public InvestmentResponse(Long id, String name, BigDecimal totalValue, BigDecimal profitability) {
        this.id = id;
        this.name = name;
        this.totalValue = totalValue;
        this.profitability = profitability;
    }
}

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
    private BigDecimal currentPrice;
    private int quantity;

    public InvestmentResponse() {
    }

    public InvestmentResponse(Long id, String name, BigDecimal totalValue, BigDecimal profitability,
            BigDecimal currentPrice, int quantity) {
        this.id = id;
        this.name = name;
        this.totalValue = totalValue;
        this.profitability = profitability;
        this.currentPrice = currentPrice;
        this.quantity = quantity;
    }
}

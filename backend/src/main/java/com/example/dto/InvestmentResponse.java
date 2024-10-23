package com.example.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class InvestmentResponse {
    private Long id;
    private String name;
    private BigDecimal totalValue;
    private BigDecimal profitability;
    private BigDecimal currentPrice;
    private int quantity;
}

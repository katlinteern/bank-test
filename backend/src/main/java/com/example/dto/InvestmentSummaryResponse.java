package com.example.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class InvestmentSummaryResponse {

    private BigDecimal totalValue;
    private BigDecimal profitability;
    private int numberOfInvestments;
}

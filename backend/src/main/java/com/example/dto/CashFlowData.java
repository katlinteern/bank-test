package com.example.dto;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CashFlowData {
    private final BigDecimal amount;
    private final Instant date;
}

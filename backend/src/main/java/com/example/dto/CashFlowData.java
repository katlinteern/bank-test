package com.example.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class CashFlowData {
    private final BigDecimal amount;
    private final Instant date;

    public CashFlowData(BigDecimal amount, Instant date) {
        this.amount = amount;
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getDate() {
        return date;
    }
}

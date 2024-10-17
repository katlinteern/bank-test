package com.example.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class CashFlowData {
    private List<BigDecimal> cashFlows;
    private List<Instant> cashFlowDates;

    public CashFlowData(List<BigDecimal> cashFlows, List<Instant> cashFlowDates) {
        this.cashFlows = cashFlows;
        this.cashFlowDates = cashFlowDates;
    }

    public List<BigDecimal> getCashFlows() {
        return cashFlows;
    }

    public List<Instant> getCashFlowDates() {
        return cashFlowDates;
    }
}

package com.example.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.CashFlowData;
import com.example.model.Investment;

@Service
public class CashFlowService {

    @Autowired
    TransactionService transactionService;

    public List<CashFlowData> collectCashFlowData(Investment investment) {
        List<CashFlowData> cashFlowData = new ArrayList<>();

        investment.getTransactions().forEach(transaction -> 
            cashFlowData.add(new CashFlowData(transactionService.calculateCashFlow(transaction), transaction.getTimestamp())));

        investment.getDividends().forEach(dividend -> 
            cashFlowData.add(new CashFlowData(dividend.getAmount(), dividend.getTimestamp())));

        BigDecimal currentValue = investment.getCurrentPrice()
                .multiply(BigDecimal.valueOf(investment.getCurrentQuantity()));
        cashFlowData.add(new CashFlowData(currentValue, Instant.now()));

        return cashFlowData;
    }

    public List<CashFlowData> filterAndSortCashFlowData(List<CashFlowData> cashFlowData) {
        return cashFlowData.stream()
                .filter(cashFlow -> cashFlow.getDate().isBefore(Instant.now())
                        || cashFlow.getDate().equals(Instant.now())) // filter out future dates
                .filter(cashFlow -> cashFlow.getAmount().compareTo(BigDecimal.ZERO) != 0) // filter out zero amounts
                .sorted(Comparator.comparing(CashFlowData::getDate)) // sort by date
                .collect(Collectors.toList());
    }

    public List<Instant> extractDates(List<CashFlowData> cashFlowDataList) {
        return cashFlowDataList.stream()
                .map(CashFlowData::getDate)
                .collect(Collectors.toList());
    }

    public List<BigDecimal> extractCashFlows(List<CashFlowData> cashFlowDataList) {
        return cashFlowDataList.stream()
                .map(CashFlowData::getAmount)
                .collect(Collectors.toList());
    }
}

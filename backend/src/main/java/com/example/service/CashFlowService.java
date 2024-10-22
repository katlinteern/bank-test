package com.example.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.CashFlowData;
import com.example.model.Investment;

@Service
public class CashFlowService {

    @Autowired
    private TransactionService transactionService;

    public List<CashFlowData> collectCashFlowData(Investment investment) {
        if (!isCurrentPriceValid(investment)) {
            return new ArrayList<>(); 
        }

        List<CashFlowData> cashFlowData = new ArrayList<>();
        processTransactions(investment, cashFlowData);
        processDividends(investment, cashFlowData);
        addCurrentValueToCashFlowData(investment, cashFlowData);

        return cashFlowData;
    }

    public List<CashFlowData> filterAndSortCashFlowData(List<CashFlowData> cashFlowData) {
        return cashFlowData.stream()
                .filter(this::isValidCashFlow)
                .sorted(Comparator.comparing(CashFlowData::getDate))
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

    private boolean isCurrentPriceValid(Investment investment) {
        return Optional.ofNullable(investment.getCurrentPrice())
                .filter(price -> price.compareTo(BigDecimal.ZERO) > 0)
                .isPresent();
    }

    private void processTransactions(Investment investment, List<CashFlowData> cashFlowData) {
        investment.getTransactions().forEach(transaction -> {
            BigDecimal cashFlowAmount = transactionService.calculateCashFlow(transaction);
            cashFlowData.add(new CashFlowData(cashFlowAmount, transaction.getTimestamp()));
        });
    }

    private void processDividends(Investment investment, List<CashFlowData> cashFlowData) {
        investment.getDividends().forEach(dividend -> {
            cashFlowData.add(new CashFlowData(dividend.getAmount(), dividend.getTimestamp()));
        });
    }

    private void addCurrentValueToCashFlowData(Investment investment, List<CashFlowData> cashFlowData) {
        BigDecimal currentValue = investment.getCurrentPrice()
                .multiply(BigDecimal.valueOf(transactionService.calculateTotalQuantity(investment.getTransactions())));
        cashFlowData.add(new CashFlowData(currentValue, Instant.now()));
    }

    private boolean isValidCashFlow(CashFlowData cashFlow) {
        return (cashFlow.getDate().isBefore(Instant.now()) || cashFlow.getDate().equals(Instant.now()))
                && cashFlow.getAmount().compareTo(BigDecimal.ZERO) != 0;
    }
}

package com.example.service;

import com.example.dto.CashFlowData;
import com.example.model.Investment;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentProfitService {

    @Autowired
    TransactionService transactionService;

    public List<CashFlowData> collectCashFlowData(Investment investment) {
        List<CashFlowData> cashFlowData = new ArrayList<>();

        investment.getTransactions().forEach(transaction -> cashFlowData
                .add(new CashFlowData(transactionService.calculateCashFlow(transaction), transaction.getTimestamp())));
        investment.getDividends()
                .forEach(dividend -> cashFlowData.add(new CashFlowData(dividend.getAmount(), dividend.getTimestamp())));

        return cashFlowData.stream()
                .sorted(Comparator.comparing(CashFlowData::getDate))
                .toList();
    }

}

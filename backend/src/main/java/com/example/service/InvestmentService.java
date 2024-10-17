package com.example.service;

import com.example.dto.CashFlowData;
import com.example.dto.response.InvestmentResponse;
import com.example.dto.response.InvestmentSummaryResponse;
import com.example.model.Investment;
import com.example.repository.InvestmentRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.utils.XirrCalculator.calculateXirr;

@Service
public class InvestmentService {
    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);

    @Autowired
    InvestmentRepository investmentRepository;

    @Autowired
    TransactionService transactionService;

    public List<InvestmentResponse> getUserInvestments(Long userId) {
        List<Investment> investments = investmentRepository.findAllByUserId(userId);

        if (investments.isEmpty()) {
            logger.warn("No investments found for user ID: {}", userId);
        }

        return investments.stream()
            .map(investment -> new InvestmentResponse(investment.getId(), investment.getName()))
            .collect(Collectors.toList());
    }

    public InvestmentSummaryResponse getUserInvestmentSummary(Long userId) {
        List<Investment> investments = investmentRepository.findAllByUserId(userId);
        logger.info("Found {} investments for user ID: {}", investments.size(), userId);
    
        if (investments.isEmpty()) {
            logger.warn("No investments available for user ID: {}", userId);
            return new InvestmentSummaryResponse(BigDecimal.ZERO, BigDecimal.ZERO, 0);
        }
    
        List<CashFlowData> allCashFlowData = investments.stream()
            .flatMap(investment -> collectCashFlowData(investment).stream())
            .sorted(Comparator.comparing(CashFlowData::getDate))
            .collect(Collectors.toList());

        BigDecimal totalXirr = calculateXirr(allCashFlowData);
    
        int numberOfInvestments = investments.size();

        return new InvestmentSummaryResponse(BigDecimal.ZERO, totalXirr, numberOfInvestments);
    }

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

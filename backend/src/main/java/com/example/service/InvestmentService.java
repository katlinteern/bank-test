package com.example.service;

import static com.example.util.XirrCalculator.calculateXirr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.CashFlowData;
import com.example.dto.InvestmentResponse;
import com.example.dto.InvestmentSummaryResponse;
import com.example.model.Investment;
import com.example.repository.InvestmentRepository;

@Service
public class InvestmentService {
    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);

    @Autowired
    InvestmentRepository investmentRepository;

    @Autowired
    CashFlowService cashFlowService;

    @Autowired
    TransactionService transactionService;

    public List<InvestmentResponse> getUserInvestments(Long userId) {
        List<Investment> investments = investmentRepository.findAllByUserId(userId);

        if (investments.isEmpty()) {
            logger.warn("No investments found for user ID: {}", userId);
        }

        return investments.stream()
                .map(this::createInvestmentResponse)
                .collect(Collectors.toList());
    }

    public InvestmentSummaryResponse getUserInvestmentSummary(Long userId) {
        List<Investment> investments = investmentRepository.findAllByUserId(userId);
        logger.info("Found {} investments for user ID: {}", investments.size(), userId);

        if (investments.isEmpty()) {
            logger.warn("No investments available for user ID: {}", userId);
            return new InvestmentSummaryResponse(BigDecimal.ZERO, BigDecimal.ZERO, 0);
        }

        return calculateInvestmentSummary(investments);
    }

    public InvestmentResponse createInvestmentResponse(Investment investment) {
        InvestmentSummaryResponse summary = calculateInvestmentSummary(List.of(investment));
        int quantity = transactionService.calculateTotalQuantity(investment.getTransactions());
        
        return new InvestmentResponse(investment.getId(), investment.getName(), summary.getTotalValue(),
                summary.getProfitability(), investment.getCurrentPrice(), quantity);
    }

    private InvestmentSummaryResponse calculateInvestmentSummary(List<Investment> investments) {
        BigDecimal totalValue = BigDecimal.ZERO;
        List<CashFlowData> cashFlowData = new ArrayList<>();

        for (Investment investment : investments) {
            totalValue = totalValue.add(calculateTotalValue(investment));
            cashFlowData.addAll(cashFlowService.collectCashFlowData(investment));
        }

        cashFlowData = cashFlowService.filterAndSortCashFlowData(cashFlowData);

        BigDecimal totalXirr;
        try {
            totalXirr = cashFlowData.isEmpty() ? BigDecimal.ZERO
                    : calculateXirr(cashFlowService.extractDates(cashFlowData),
                            cashFlowService.extractCashFlows(cashFlowData));
        } catch (IllegalArgumentException e) {
            logger.warn("XIRR calculation failed: {}", e.getMessage());
            totalXirr = null;
        }

        BigDecimal profitability = Optional.ofNullable(totalXirr)
                .map(xirr -> xirr.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP))
                .orElse(null);

        return new InvestmentSummaryResponse(totalValue, profitability, investments.size());
    }

    public BigDecimal calculateTotalValue(Investment investment) {
        BigDecimal currentPrice = investment.getCurrentPrice();
        int totalQuantity = transactionService.calculateTotalQuantity(investment.getTransactions());

        return currentPrice.multiply(BigDecimal.valueOf(totalQuantity));
    }

}

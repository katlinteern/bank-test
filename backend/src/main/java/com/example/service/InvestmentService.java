package com.example.service;

import static com.example.util.XirrCalculator.calculateXirr;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
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
            return new InvestmentSummaryResponse(null, null, 0);
        }

        return createInvestmentSummary(investments);
    }

    private InvestmentResponse createInvestmentResponse(Investment investment) {
        InvestmentSummaryResponse summary = createInvestmentSummary(List.of(investment));
        int quantity = transactionService.calculateTotalQuantity(investment.getTransactions());

        return new InvestmentResponse(investment.getId(), investment.getName(), summary.getTotalValue(),
                summary.getProfitability(), investment.getCurrentPrice(), quantity);
    }

    private InvestmentSummaryResponse createInvestmentSummary(List<Investment> investments) {
        BigDecimal totalValue = investments.stream().map(this::calculateTotalValue).reduce(BigDecimal.ZERO,
                BigDecimal::add);
        List<CashFlowData> cashFlowData = cashFlowService.collectAndFilterCashFlows(investments);

        BigDecimal profitability = calculateProfitability(cashFlowData);

        return new InvestmentSummaryResponse(totalValue, profitability, investments.size());
    }

    private BigDecimal calculateTotalValue(Investment investment) {
        if (investment == null) {
            return BigDecimal.valueOf(0);
        }

        BigDecimal currentPrice = investment.getCurrentPrice();
        int totalQuantity = transactionService.calculateTotalQuantity(investment.getTransactions());

        return currentPrice.multiply(BigDecimal.valueOf(totalQuantity));
    }

    private BigDecimal calculateProfitability(List<CashFlowData> cashFlowData) {
        if (cashFlowData.isEmpty()) {
            return null;
        }
        try {
            BigDecimal totalXirr = calculateXirr(
                    cashFlowService.extractDates(cashFlowData),
                    cashFlowService.extractCashFlows(cashFlowData));

            return totalXirr.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

        } catch (IllegalArgumentException e) {
            logger.warn("XIRR calculation failed: {}", e.getMessage());
            return null;
        }
    }

}

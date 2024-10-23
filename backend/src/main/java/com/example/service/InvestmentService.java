package com.example.service;

import static com.example.util.XirrCalculator.calculateXirr;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        return createInvestmentSummary(investments);
    }

    public InvestmentResponse createInvestmentResponse(Investment investment) {
        InvestmentSummaryResponse summary = createInvestmentSummary(List.of(investment));
        int quantity = transactionService.calculateTotalQuantity(investment.getTransactions());

        return new InvestmentResponse(investment.getId(), investment.getName(), summary.getTotalValue(),
                summary.getProfitability(), investment.getCurrentPrice(), quantity);
    }

    private InvestmentSummaryResponse createInvestmentSummary(List<Investment> investments) {
        BigDecimal totalValue = investments.stream().map(this::calculateTotalValue).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<CashFlowData> cashFlowData = cashFlowService.collectAndFilterCashFlows(investments);

        BigDecimal totalXirr = calculateTotalXirr(cashFlowData);
        BigDecimal profitability = calculateProfitability(totalXirr);

        return new InvestmentSummaryResponse(totalValue, profitability, investments.size());
    }

    private BigDecimal calculateTotalXirr(List<CashFlowData> cashFlowData) {
        try {
            return cashFlowData.isEmpty() ? BigDecimal.ZERO
                    : calculateXirr(cashFlowService.extractDates(cashFlowData),
                            cashFlowService.extractCashFlows(cashFlowData));
        } catch (IllegalArgumentException e) {
            logger.warn("XIRR calculation failed: {}", e.getMessage());
            return null;
        }
    }

    private BigDecimal calculateProfitability(BigDecimal totalXirr) {
        return Optional.ofNullable(totalXirr)
                .map(xirr -> xirr.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP))
                .orElse(null);
    }

    public BigDecimal calculateTotalValue(Investment investment) {
        BigDecimal currentPrice = investment.getCurrentPrice();
        int totalQuantity = transactionService.calculateTotalQuantity(investment.getTransactions());

        return currentPrice.multiply(BigDecimal.valueOf(totalQuantity));
    }
}

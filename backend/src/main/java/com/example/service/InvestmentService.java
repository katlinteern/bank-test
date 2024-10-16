package com.example.service;

import com.example.dto.ProfitResult;
import com.example.dto.response.InvestmentResponse;
import com.example.dto.response.InvestmentSummaryResponse;
import com.example.model.Investment;
import com.example.repository.InvestmentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InvestmentService {
    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);

    private final InvestmentRepository investmentRepository;
    private final ProfitService profitService;

    public InvestmentService(InvestmentRepository investmentRepository, ProfitService profitService) {
        this.investmentRepository = investmentRepository;
        this.profitService = profitService;
    }

    public List<InvestmentResponse> getInvestmentsByUserId(Long userId) {
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

        return calculateSummary(investments);
    }

    private InvestmentSummaryResponse calculateSummary(List<Investment> investments) {
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalXirr = BigDecimal.ZERO;

        for (Investment investment : investments) {
            ProfitResult result = profitService.calculateInvestmentProfit(investment);
            totalValue = totalValue.add(result.getTotalValue());
            totalXirr = totalXirr.add(result.getXirr());
        }

        int numberOfInvestments = investments.size();
        return new InvestmentSummaryResponse(totalValue, totalXirr, numberOfInvestments);
    }
}

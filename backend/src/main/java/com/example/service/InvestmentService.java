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

        // Log if no investments found for the user
        if (investments.isEmpty()) {
            logger.warn("No investments found for user ID: {}", userId);
        }

        return investments.stream()
            .map(investment -> new InvestmentResponse(investment.getId(), investment.getName()))
            .collect(Collectors.toList());
    }

    public InvestmentSummaryResponse getUserInvestmentSummary(Long userId) {
        List<Investment> investments = investmentRepository.findAllByUserId(userId);

        // Log the number of investments found
        logger.info("Found {} investments for user ID: {}", investments.size(), userId);

        BigDecimal totalProfit = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;

        // Check if there are any investments before processing
        if (investments.isEmpty()) {
            logger.warn("No investments available for user ID: {}", userId);
            return null; 
        }

        for (Investment investment : investments) {
            ProfitResult result = profitService.calculateInvestmentProfit(investment);
            totalProfit = totalProfit.add(result.getProfit());
            totalValue = totalValue.add(result.getTotalValue());
        }

        // Avoid division by zero for profit percentage calculation
        Double profitPercentage = (totalValue.compareTo(BigDecimal.ZERO) > 0) ?
            profitService.calculateProfitPercentage(totalProfit, totalValue) : 0.0;

        int numberOfInvestments = investments.size();

        // Return a new InvestmentSummaryResponse including all necessary data
        return new InvestmentSummaryResponse(totalValue, totalProfit, profitPercentage, numberOfInvestments);
    }
}

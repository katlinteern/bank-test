package com.example.service;

import com.example.dto.ProfitabilityResult;
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
    private final ProfitabilityService profitabilityService;

    public InvestmentService(InvestmentRepository investmentRepository, ProfitabilityService profitabilityService) {
        this.investmentRepository = investmentRepository;
        this.profitabilityService = profitabilityService;
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

        BigDecimal totalProfitability = BigDecimal.ZERO;
        BigDecimal totalInvestment = BigDecimal.ZERO;

        // Check if there are any investments before processing
        if (investments.isEmpty()) {
            logger.warn("No investments available for user ID: {}", userId);
            return null; 
        }

        for (Investment investment : investments) {
            ProfitabilityResult result = profitabilityService.calculateInvestmentProfitability(investment);
            totalProfitability = totalProfitability.add(result.getProfitability());
            totalInvestment = totalInvestment.add(result.getTotalInvestment());
        }

        // Avoid division by zero for profitability percentage calculation
        Double profitabilityPercentage = (totalInvestment.compareTo(BigDecimal.ZERO) > 0) ?
            profitabilityService.calculateProfitabilityPercentage(totalProfitability, totalInvestment) : 0.0;

        int numberOfInvestments = investments.size();

        // Return a new InvestmentSummaryResponse including all necessary data
        return new InvestmentSummaryResponse(totalInvestment, totalProfitability, profitabilityPercentage, numberOfInvestments);
    }
}

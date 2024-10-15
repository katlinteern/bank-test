package com.example.service;

import com.example.dto.ProfitabilityResult;
import com.example.dto.response.InvestmentResponse;
import com.example.dto.response.PortfolioProfitabilityResponse;
import com.example.model.Investment;
import com.example.repository.InvestmentRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentService {
    Logger logger = LoggerFactory.getLogger(InvestmentService.class);

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private ProfitabilityService profitabilityService;

    public List<InvestmentResponse> getInvestmentsByUserId(Long userId) {
        List<Investment> investments = investmentRepository.findAllByUserId(userId);
        
        List<InvestmentResponse> investmentResponses = investments.stream()
            .map(investment -> new InvestmentResponse(investment.getId(), investment.getName()))
            .collect(Collectors.toList());
        
        return investmentResponses;
    }

    public PortfolioProfitabilityResponse getPortfolioProfitability() {
        List<Investment> investments = investmentRepository.findAll();

        BigDecimal totalProfitability = BigDecimal.ZERO;
        BigDecimal totalInvestment = BigDecimal.ZERO;

        for (Investment investment : investments) {
            ProfitabilityResult result = profitabilityService.calculateInvestmentProfitability(investment);
            totalProfitability = totalProfitability.add(result.getProfitability());
            totalInvestment = totalInvestment.add(result.getTotalInvestment());
        }

        Double profitabilityPercentage = profitabilityService.calculateProfitabilityPercentage(totalProfitability, totalInvestment);

        return new PortfolioProfitabilityResponse(totalProfitability, profitabilityPercentage);
    }
    
}

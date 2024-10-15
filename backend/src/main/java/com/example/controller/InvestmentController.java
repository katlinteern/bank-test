package com.example.controller;

import com.example.dto.response.InvestmentResponse;
import com.example.dto.response.PortfolioProfitabilityResponse;
import com.example.service.InvestmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {
    Logger logger = LoggerFactory.getLogger(InvestmentController.class);

    @Autowired
    private InvestmentService investmentService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvestmentResponse>> getInvestmentsByUserId(@PathVariable Long userId) {
        logger.info("Fetching investments for user ID: {}", userId);
        
        List<InvestmentResponse> investments = investmentService.getInvestmentsByUserId(userId);

        return ResponseEntity.ok(investments);
    }

    @GetMapping("/profitability/")
    public ResponseEntity<PortfolioProfitabilityResponse> getPortfolioProfitability() {
        logger.info("Fetching profitability");

        PortfolioProfitabilityResponse investments = investmentService.getPortfolioProfitability();

        return ResponseEntity.ok(investments);
    }
}

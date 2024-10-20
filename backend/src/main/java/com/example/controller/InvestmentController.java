package com.example.controller;

import com.example.dto.InvestmentResponse;
import com.example.dto.InvestmentSummaryResponse;
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

    // User existance simulation with userId 1
    private boolean userExists(Long userId) {
        return userId == 1; 
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<InvestmentResponse>> getInvestmentsByUserId(@PathVariable Long userId) {
        logger.info("Fetching investments for user ID: {}", userId);

        if (!userExists(userId)) {
            logger.warn("User ID: {} does not exist", userId);
            return ResponseEntity.notFound().build(); 
        }

        List<InvestmentResponse> investments = investmentService.getUserInvestments(userId);

        if (investments.isEmpty()) {
            logger.warn("No investments found for user ID: {}", userId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(investments); 
    }

    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<InvestmentSummaryResponse> getUserInvestmentSummary(@PathVariable Long userId) {
        logger.info("Fetching investment summary for user ID: {}", userId);

        if (!userExists(userId)) {
            logger.warn("User ID: {} does not exist", userId);
            return ResponseEntity.notFound().build(); 
        }

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        if (summary == null) {
            logger.warn("No investment summary available for user ID: {}", userId);
            return ResponseEntity.noContent().build(); 
        }

        return ResponseEntity.ok(summary);
    }
}

package com.example.controller;

import com.example.dto.response.InvestmentResponse;
import com.example.dto.response.InvestmentSummaryResponse;
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
        
        List<InvestmentResponse> investments = investmentService.getUserInvestments(userId);

        if (investments.isEmpty()) {
            logger.warn("No investments found for user ID: {}", userId);
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(investments); // 200 OK
    }

    @GetMapping("/user/{userId}/summary") // Updated endpoint to reflect aggregation
    public ResponseEntity<InvestmentSummaryResponse> getUserInvestmentSummary(@PathVariable Long userId) {
        logger.info("Fetching investment summary for user ID: {}", userId);

        InvestmentSummaryResponse summary = investmentService.getUserInvestmentSummary(userId);

        if (summary == null) {
            logger.warn("No investment summary available for user ID: {}", userId);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }

        return ResponseEntity.ok(summary); // 200 OK
    }
}

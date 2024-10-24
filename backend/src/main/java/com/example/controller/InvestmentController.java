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
    private static final Logger logger = LoggerFactory.getLogger(InvestmentController.class);

    @Autowired InvestmentService investmentService;

    /**
     * Fetches a list of investments for a specific user identified by userId.
     *
     * @param userId the ID of the user for whom to fetch investments
     * @return ResponseEntity containing a list of InvestmentResponse objects if found,
     *         404 Not Found if the user does not exist, or 204 No Content if no investments are found
     */
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

    /**
     * Fetches the investment summary for a specific user identified by userId.
     *
     * @param userId the ID of the user for whom to fetch the investment summary
     * @return ResponseEntity containing an InvestmentSummaryResponse if found,
     *         404 Not Found if the user does not exist, or 204 No Content if no summary is available
     */
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

    /**
     * Simulates the existence check for a user based on the userId.
     *
     * @param userId the ID of the user to check
     * @return true if the user exists (in this simulation, only userId 1 exists), false otherwise
     */
    private boolean userExists(Long userId) {
        return userId != null && userId == 1; 
    }
}

package com.example.controller;

import com.example.model.Investment;
import com.example.service.InvestmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments")
public class InvestmentController {
    @Autowired
    private InvestmentService investmentService;

    @GetMapping("/user/{userId}")
    public List<Investment> getInvestmentsByUserId(@PathVariable Long userId) {
        return investmentService.getInvestmentsByUserId(userId);
    }
}

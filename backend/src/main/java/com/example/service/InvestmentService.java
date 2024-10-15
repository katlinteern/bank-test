package com.example.service;

import com.example.dto.response.InvestmentResponse;
import com.example.model.Investment;
import com.example.repository.InvestmentRepository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentService {

     @Autowired
    private InvestmentRepository investmentRepository;

    public List<InvestmentResponse> getInvestmentsByUserId(Long userId) {
        List<Investment> investments = investmentRepository.findAllByUserId(userId);
        
        List<InvestmentResponse> investmentResponses = investments.stream()
            .map(investment -> new InvestmentResponse(investment.getId(), investment.getName()))
            .collect(Collectors.toList());
        
        return investmentResponses;
    }

}

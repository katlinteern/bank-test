package com.example.service;

import com.example.model.Investment;
import com.example.repository.InvestmentRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvestmentService {

     @Autowired
    private InvestmentRepository investmentRepository;

    public List<Investment> getInvestmentsByUserId(Long userId) {
        return investmentRepository.findByUserId(userId);
    }


}

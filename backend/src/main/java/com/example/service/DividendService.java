package com.example.service;

import com.example.model.Dividend;
import com.example.repository.DividendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DividendService {

    @Autowired
    private DividendRepository dividendRepository;

    public List<Dividend> getDividendsByInvestmentId(Long investmentId) {
        return dividendRepository.findByInvestmentId(investmentId);
    }

}

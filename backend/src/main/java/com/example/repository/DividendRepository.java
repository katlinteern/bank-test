package com.example.repository;

import com.example.model.Dividend;
import com.example.model.Investment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DividendRepository extends JpaRepository<Dividend, Long> {
    List<Dividend> findByInvestmentId(Long investmentId);

    List<Dividend> findByInvestment(Investment investment);
}

package com.example.repository;

import com.example.model.Investment;
import com.example.model.Transaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByInvestmentId(Long investmentId);

    List<Transaction> findByInvestment(Investment investment);
}

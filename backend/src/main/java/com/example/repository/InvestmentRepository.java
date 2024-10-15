package com.example.repository;

import com.example.model.Investment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findAllByUserId(Long userId);

}

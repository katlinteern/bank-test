package com.example.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "investments")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal currentPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "investment", cascade = CascadeType.PERSIST)
    private List<Transaction> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "investment", cascade = CascadeType.PERSIST)
    private List<Dividend> dividends = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        // Check if the currentPrice is negative
        if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative or null");
        }
        this.currentPrice = currentPrice;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public List<Dividend> getDividends() {
        return dividends;
    }

    public void setDividends(List<Dividend> dividends) {
        this.dividends = dividends;
    }
}

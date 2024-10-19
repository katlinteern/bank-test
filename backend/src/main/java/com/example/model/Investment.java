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

    @Column(nullable = false)
    private int currentQuantity = 0;

    @OneToMany(mappedBy = "investment", cascade = CascadeType.PERSIST) // Avoid removing transactions on investment removal
    private List<Transaction> transactions = new ArrayList<>(); // Changed to List for order preservation

    @OneToMany(mappedBy = "investment", cascade = CascadeType.PERSIST)
    private List<Dividend> dividends = new ArrayList<>(); // Changed to List for consistency

    // Default constructor
    public Investment() {
    }

    // Getters and setters
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
        this.currentPrice = currentPrice;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
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

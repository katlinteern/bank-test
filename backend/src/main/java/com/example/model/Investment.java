package com.example.model;

import jakarta.persistence.*;

import java.util.Set;


@Entity
@Table(name = "investments")
public class Investment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name; 

    @OneToMany(mappedBy = "investment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Transaction> transactions;

    @OneToMany(mappedBy = "investment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Dividend> dividends;

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

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Set<Dividend> getDividends() {
        return dividends;
    }

    public void setDividends(Set<Dividend> dividends) {
        this.dividends = dividends;
    }
}

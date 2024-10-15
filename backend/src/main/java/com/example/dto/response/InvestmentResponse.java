package com.example.dto.response;

import lombok.Data;

/**
 * Data Transfer Object for Transaction response
 */
@Data
public class InvestmentResponse {

    private Long id;
    private String name;

    public InvestmentResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
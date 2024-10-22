package com.example.generator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataGenerator implements CommandLineRunner {

    @Autowired
    private InvestmentGenerator investmentGenerator;

    @Override
    public void run(String... args) {
        investmentGenerator.generateInvestmentData();
    }

}

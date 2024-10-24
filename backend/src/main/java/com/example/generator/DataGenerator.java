package com.example.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataGenerator implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataGenerator.class);

    @Autowired
    private InvestmentGenerator investmentGenerator;

    @Override
    public void run(String... args) {
        logger.info("Started generating data...");

        investmentGenerator.generateInvestmentData();

        logger.info("Finished generating data.");
    }

}

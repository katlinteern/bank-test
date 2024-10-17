package com.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.dto.CashFlowData;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class XirrCalculator {

    private static final Logger logger = LoggerFactory.getLogger(XirrCalculator.class);
    private static final int MAX_ITERATIONS = 1000;
    private static final double PRECISION = 1e-6;

    public static BigDecimal calculateXirr(List<CashFlowData> cashFlowDataList) {
        logger.info("Starting XIRR calculation...");

        // Koguge kuupäevad ja rahavoogud
        List<Instant> dates = cashFlowDataList.stream()
            .map(CashFlowData::getDate)
            .toList();
        List<BigDecimal> cashFlows = cashFlowDataList.stream()
            .map(CashFlowData::getAmount)
            .toList();

        validateInput(dates, cashFlows);

        logger.info("Initial guess for XIRR: 0.1");
        double guessRate = 0.1;
        double rate = guessRate;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            double npv = calculateNpv(rate, dates, cashFlows);
            double npvDerivative = calculateNpvDerivative(rate, dates, cashFlows);

            logger.info("Precision {}:", PRECISION);
            logger.info("Iteration {}: rate = {}, NPV = {}, NPV derivative = {}", i, rate, npv, npvDerivative);

            if (Double.isNaN(npv) || Double.isInfinite(npv)) {
                logger.error("NPV is NaN or Infinite at iteration {}: rate = {}", i, rate);
                throw new ArithmeticException("NPV calculation resulted in NaN or Infinite");
            }

            if (Double.isNaN(npvDerivative) || Double.isInfinite(npvDerivative)) {
                logger.error("NPV derivative is NaN or Infinite at iteration {}: rate = {}", i, rate);
                throw new ArithmeticException("NPV derivative calculation resulted in NaN or Infinite");
            }

            // Newton-Raphson meetod
            if (npvDerivative == 0) {
                logger.error("NPV derivative is zero at iteration {}: rate = {}", i, rate);
                throw new ArithmeticException("Division by zero in Newton-Raphson method");
            }

            double newRate = rate - npv / npvDerivative;
            logger.info("New rate calculated: {}", newRate);

            if (newRate <= -1.0) {
                logger.error("Calculated rate is less than or equal to -1. XIRR calculation cannot proceed.");
                throw new ArithmeticException("Calculated rate is less than or equal to -1. Calculation stopped.");
            }

            if (Math.abs(newRate - rate) < PRECISION) {
                logger.info("XIRR calculation successful. Final rate: {}", newRate);
                return BigDecimal.valueOf(newRate);
            }
            rate = newRate;
        }
        logger.error("XIRR calculation failed - maximum iterations reached.");
        throw new RuntimeException("XIRR calculation did not work - max iterations achieved.");
    }

    private static double calculateNpv(double rate, List<Instant> dates, List<BigDecimal> cashFlows) {
        double npv = 0.0;
        long firstDate = dates.get(0).toEpochMilli();

        logger.info("Calculating NPV with rate: {}", rate);

        // Kontrolli, et intressimäär ei ole -1 või madalam
        if (rate <= -1.0) {
            logger.error("Invalid rate: {}. Rate must be greater than -1 to prevent division by zero.", rate);
            throw new ArithmeticException("Rate must be greater than -1.");
        }

        for (int i = 0; i < cashFlows.size(); i++) {
            long daysDifference = (dates.get(i).toEpochMilli() - firstDate) / (1000 * 60 * 60 * 24);
            npv += cashFlows.get(i).doubleValue() / Math.pow(1 + rate, (double) daysDifference / 365);
        }

        return npv;
    }

    private static double calculateNpvDerivative(double rate, List<Instant> dates, List<BigDecimal> cashFlows) {
        double npvDerivative = 0.0;
        long firstDate = dates.get(0).toEpochMilli();
        logger.info("Calculating NPV derivative with rate: {}", rate);

        // Kontrolli, et intressimäär ei ole -1 või madalam
        if (rate <= -1.0) {
            logger.error("Invalid rate: {}. Rate must be greater than -1 to prevent division by zero.", rate);
            throw new ArithmeticException("Rate must be greater than -1.");
        }

        for (int i = 0; i < cashFlows.size(); i++) {
            long daysDifference = (dates.get(i).toEpochMilli() - firstDate) / (1000 * 60 * 60 * 24);
            npvDerivative -= (daysDifference / 365.0) * cashFlows.get(i).doubleValue() / Math.pow(1 + rate, (double) daysDifference / 365 + 1);
        }
        return npvDerivative;
    }

    private static void validateInput(List<Instant> dates, List<BigDecimal> cashFlows) {
        logger.info("Validating input: dates={}, cashFlows={}", dates, cashFlows);
        if (dates == null || cashFlows == null || dates.size() != cashFlows.size()) {
            logger.error("Input validation failed: dates or cash flows are null or of different size.");
            throw new IllegalArgumentException("Number of dates and cash flows has to be the same.");
        }
        logger.info("Input validation successful.");
    }
}

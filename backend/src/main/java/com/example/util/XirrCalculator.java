package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class XirrCalculator {

    private static final Logger logger = LoggerFactory.getLogger(XirrCalculator.class);
    private static final int MAX_ITERATIONS = 1000;
    private static final double PRECISION = 1e-6;
    private static final double DEFAULT_RATE = 0.1;
    private static final double SMALL_DERIVATIVE = 1e-4;
    private static final int DAYS_IN_YEAR = 365;

    public static BigDecimal calculateXirr(List<Instant> dates, List<BigDecimal> cashFlows) {
        logger.info("Starting XIRR calculation...");

        if (!Validator.isXirrInputValid(dates, cashFlows)) {
            return null;
        }

        double rate = DEFAULT_RATE;

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            Double npv = calculateNpv(rate, dates, cashFlows);
            Double npvDerivative = calculateNpvDerivative(rate, dates, cashFlows);

            logger.info("Iteration {}: rate = {}, NPV = {}, NPV derivative = {}", iteration, rate, npv, npvDerivative);

            if (isInvalidNpvOrDerivative(npv, npvDerivative)) {
                logger.warn("Invalid NPV or derivative at iteration {}. Returning last known rate.", iteration);
                return iteration == 0 ? null : BigDecimal.valueOf(rate);
            }

            double newRate = rate - npv / npvDerivative;

            if (isRateConverged(rate, newRate)) {
                logger.info("XIRR calculation successful. Final rate: {}", newRate);
                return BigDecimal.valueOf(newRate);
            }

            rate = newRate;
        }

        logger.warn("Max iterations reached. Returning last known rate: {}", rate);
        return BigDecimal.valueOf(rate);
    }

    private static boolean isInvalidNpvOrDerivative(Double npv, Double npvDerivative) {
        return npv == null || npvDerivative == null || Double.isNaN(npv) || Double.isInfinite(npv)
                || Double.isNaN(npvDerivative) || Double.isInfinite(npvDerivative);
    }

    private static boolean isRateConverged(double rate, double newRate) {
        return Math.abs(newRate - rate) < PRECISION;
    }

    private static Double calculateNpv(double rate, List<Instant> dates, List<BigDecimal> cashFlows) {
        if (rate <= -1.0) {
            logger.warn("Rate is less than or equal to -1. Returning null.");
            return null;
        }

        double npv = 0.0;

        for (int i = 0; i < cashFlows.size(); i++) {
            long daysDifference = ChronoUnit.DAYS.between(dates.get(0), dates.get(i));
            npv += cashFlows.get(i).doubleValue() / Math.pow(1 + rate, (double) daysDifference / DAYS_IN_YEAR);
        }

        return npv;
    }

    private static Double calculateNpvDerivative(double rate, List<Instant> dates, List<BigDecimal> cashFlows) {
        if (rate <= -1.0) {
            logger.warn("Rate is less than or equal to -1. Returning null.");
            return null;
        }

        double npvDerivative = 0.0;

        for (int i = 0; i < cashFlows.size(); i++) {
            long daysDifference = ChronoUnit.DAYS.between(dates.get(0), dates.get(i));
            double cashFlow = cashFlows.get(i).doubleValue();

            if (cashFlow != 0) {
                npvDerivative -= (daysDifference / (double) DAYS_IN_YEAR) * cashFlow 
                                  / Math.pow(1 + rate, (double) daysDifference / DAYS_IN_YEAR + 1);
            }
        }

        return (npvDerivative == 0) ? SMALL_DERIVATIVE : npvDerivative;
    }
}

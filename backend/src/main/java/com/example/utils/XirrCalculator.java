package com.example.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
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

        if (!isValidInput(dates, cashFlows)) {
            return null;
        }

        double rate = DEFAULT_RATE;
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            Double npv = calculateNpv(rate, dates, cashFlows);
            Double npvDerivative = calculateNpvDerivative(rate, dates, cashFlows);

            logger.info("Iteration {}: rate = {}, NPV = {}, NPV derivative = {}", i, rate, npv, npvDerivative);

            if (isInvalidNpvOrDerivative(npv, npvDerivative)) {
                logger.warn("Invalid NPV or derivative at iteration {}. Returning last known rate.", i);
                return i == 0 ? null : BigDecimal.valueOf(rate);
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

    private static boolean isValidInput(List<Instant> dates, List<BigDecimal> cashFlows) {
        if (dates == null || cashFlows == null || dates.size() != cashFlows.size()) {
            logger.error("Invalid input: dates and cash flows must be non-null and of the same size.");
            return false;
        }
        if (cashFlows.size() < 2) {
            logger.error("Not enough cash flow data for XIRR calculation.");
            return false;
        }

        // Check if all cash flows are zero
        if (cashFlows.stream().allMatch(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) == 0)) {
            logger.warn("All cash flows are zero. Returning null.");
            return false;
        }

        // Check if all cash flows are negative
        if (cashFlows.stream().allMatch(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) < 0)) {
            logger.warn("All cash flows are negative. Returning null.");
            return false;
        }

        // Check if all cash flows are positive
        if (cashFlows.stream().allMatch(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) > 0)) {
            logger.warn("All cash flows are positive. Returning null.");
            return false;
        }

        logger.warn("Dates {}", dates);
        logger.warn("Dates size {}", dates.size());

        // Check if dates are in the correct order
        for (int i = 1; i < dates.size(); i++) {
            if (dates.get(i).isBefore(dates.get(i - 1))) {
                logger.warn("i - 1 {}", i - 1);
                logger.warn("i {}", i);
                logger.warn("Kp i-1 {}, i {}", dates.get(i-1), dates.get(i));

                logger.warn("Dates are not in chronological order. Returning null.");
                return false;
            }
        }

        return true;
    }

    private static boolean isInvalidNpvOrDerivative(Double npv, Double npvDerivative) {
        return npv == null || npvDerivative == null
                || Double.isNaN(npv) || Double.isInfinite(npv)
                || Double.isNaN(npvDerivative) || Double.isInfinite(npvDerivative);
    }

    private static boolean isRateConverged(double rate, double newRate) {
        return Math.abs(newRate - rate) < PRECISION;
    }

    private static Double calculateNpv(double rate, List<Instant> dates, List<BigDecimal> cashFlows) {
        long firstDate = dates.get(0).toEpochMilli();
        double npv = 0.0;

        if (rate <= -1.0) {
            logger.warn("Rate is less than or equal to -1. Returning null.");
            return null;
        }

        for (int i = 0; i < cashFlows.size(); i++) {
            long daysDifference = (dates.get(i).toEpochMilli() - firstDate) / (1000 * 60 * 60 * 24);
            npv += cashFlows.get(i).doubleValue() / Math.pow(1 + rate, (double) daysDifference / DAYS_IN_YEAR);
        }

        return npv;
    }

    private static Double calculateNpvDerivative(double rate, List<Instant> dates, List<BigDecimal> cashFlows) {
        long firstDate = dates.get(0).toEpochMilli();
        double npvDerivative = 0.0;

        if (rate <= -1.0) {
            logger.warn("Rate is less than or equal to -1. Returning null.");
            return null;
        }

        for (int i = 0; i < cashFlows.size(); i++) {
            long daysDifference = (dates.get(i).toEpochMilli() - firstDate) / (1000 * 60 * 60 * 24);
            double cashFlow = cashFlows.get(i).doubleValue();

            if (cashFlow == 0)
                continue;

            npvDerivative -= (daysDifference / (double) DAYS_IN_YEAR) * cashFlow
                    / Math.pow(1 + rate, (double) daysDifference / DAYS_IN_YEAR + 1);
        }

        return (npvDerivative == 0) ? SMALL_DERIVATIVE : npvDerivative;
    }
}

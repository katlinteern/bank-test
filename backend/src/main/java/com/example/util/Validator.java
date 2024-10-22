package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Validator {
    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

    public static boolean isXirrInputValid(List<Instant> dates, List<BigDecimal> cashFlows) {
        if (dates == null || cashFlows == null || dates.size() != cashFlows.size()) {
            logger.error("Invalid input: dates and cash flows must be non-null and of the same size.");
            return false;
        }
        if (cashFlows.size() < 2) {
            logger.error("Not enough cash flow data. At least two cash flows are required.");
            return false;
        }
        if (cashFlows.stream().allMatch(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) == 0)) {
            logger.warn("All cash flows are zero.");
            return false;
        }
        if (cashFlows.stream().allMatch(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) <= 0)) {
            logger.warn("All cash flows are negative.");
            return false;
        }
        if (cashFlows.stream().allMatch(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) >= 0)) {
            logger.warn("All cash flows are positive.");
            return false;
        }
        for (int i = 1; i < dates.size(); i++) {
            if (dates.get(i).isBefore(dates.get(i - 1))) {
                logger.warn("Dates are not in chronological order: {} follows {}", dates.get(i), dates.get(i - 1));
                return false;
            }
        }
        return true;
    }

    public static boolean isRateValid(double rate) {
        if (rate <= -1.0) {
            logger.warn("Rate is less than or equal to -1. Returning null.");
            return false;
        }
        return true;
    }
    
    public static boolean isInvalidNpvOrDerivative(Double npv, Double npvDerivative) {
        return npv == null || npvDerivative == null || Double.isNaN(npv) || Double.isInfinite(npv)
                || Double.isNaN(npvDerivative) || Double.isInfinite(npvDerivative);
    }

    public static boolean isRateConverged(double rate, double newRate, double precision) {
        return Math.abs(newRate - rate) < precision;
    }
}

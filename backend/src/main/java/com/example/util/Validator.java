package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class Validator {
    private static final Logger logger = LoggerFactory.getLogger(Validator.class);

    public static boolean isXirrInputValid(List<Instant> dates, List<BigDecimal> cashFlows) {
        return hasValidSize(dates, cashFlows) 
                && !areCashFlowsAllZero(cashFlows) 
                && !areCashFlowsAllNegative(cashFlows)
                && !areCashFlowsAllPositive(cashFlows) 
                && areDatesInChronologicalOrder(dates);
    }

    public static boolean hasValidSize(List<Instant> dates, List<BigDecimal> cashFlows) {
        boolean valid = dates != null && cashFlows != null && dates.size() == cashFlows.size() && cashFlows.size() >= 2;
        if (!valid) {
            logger.error("Invalid input: dates and cash flows must be non-null, of the same size, and at least two cash flows.");
        }
        return valid;
    }

    public static boolean areCashFlowsAllZero(List<BigDecimal> cashFlows) {
        return cashFlows.stream().allMatch(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) == 0);
    }

    public static boolean areCashFlowsAllNegative(List<BigDecimal> cashFlows) {
        return cashFlows.stream().allMatch(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) < 0);
    }

    public static boolean areCashFlowsAllPositive(List<BigDecimal> cashFlows) {
        return cashFlows.stream().allMatch(cashFlow -> cashFlow.compareTo(BigDecimal.ZERO) > 0);
    }

    public static boolean areDatesInChronologicalOrder(List<Instant> dates) {
        for (int i = 1; i < dates.size(); i++) {
            if (dates.get(i).isBefore(dates.get(i - 1))) {
                logger.warn("Dates are not in chronological order: {} follows {}", dates.get(i), dates.get(i - 1));
                return false;
            }
        }
        return true;
    }
}

package com.example.utils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class XirrCalculator {

    private static final int MAX_ITERATIONS = 1000; 
    private static final double PRECISION = 1e-6; 

    public static BigDecimal calculateXirr(List<Instant> dates, List<BigDecimal> cashFlows) {
        validateInput(dates, cashFlows); 

        double guessRate = 0.1; 
        double rate = guessRate; 

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            double npv = calculateNpv(rate, dates, cashFlows);
            double npvDerivative = calculateNpvDerivative(rate, dates, cashFlows); 

            double newRate = rate - npv / npvDerivative; 

            if (Math.abs(newRate - rate) < PRECISION) {
                return BigDecimal.valueOf(newRate); 
            }
            rate = newRate; 
        }
        
        throw new RuntimeException("XIRR calculation failed - maximum iterations exceeded."); 
    }

    private static double calculateNpv(double rate, List<Instant> dates, List<BigDecimal> cashFlows) {
        double npv = 0.0;
        long firstDate = dates.get(0).toEpochMilli(); 

        for (int i = 0; i < cashFlows.size(); i++) {
            long daysDifference = (dates.get(i).toEpochMilli() - firstDate) / (1000 * 60 * 60 * 24); 
            npv += cashFlows.get(i).doubleValue() / Math.pow(1 + rate, (double) daysDifference / 365); 
        }
        
        return npv; 
    }

    private static double calculateNpvDerivative(double rate, List<Instant> dates, List<BigDecimal> cashFlows) {
        double npvDerivative = 0.0;
        long firstDate = dates.get(0).toEpochMilli(); 

        for (int i = 0; i < cashFlows.size(); i++) {
            long daysDifference = (dates.get(i).toEpochMilli() - firstDate) / (1000 * 60 * 60 * 24); 
            npvDerivative -= (daysDifference / 365.0) * cashFlows.get(i).doubleValue() / Math.pow(1 + rate, (double) daysDifference / 365 + 1); 
        }
        
        return npvDerivative; 
    }

    private static void validateInput(List<Instant> dates, List<BigDecimal> cashFlows) {
        if (dates == null || cashFlows == null || dates.size() != cashFlows.size()) {
            throw new IllegalArgumentException("The number of dates and cash flows must be the same and cannot be null."); 
        }
    }
}

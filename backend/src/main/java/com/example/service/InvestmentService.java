package com.example.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.CashFlowData;
import com.example.dto.InvestmentResponse;
import com.example.dto.InvestmentSummaryResponse;
import com.example.model.Investment;
import com.example.repository.InvestmentRepository;

import static com.example.utils.XirrCalculator.calculateXirr;

@Service
public class InvestmentService {
    private static final Logger logger = LoggerFactory.getLogger(InvestmentService.class);

    @Autowired
    InvestmentRepository investmentRepository;

    @Autowired
    TransactionService transactionService;

    public List<InvestmentResponse> getUserInvestments(Long userId) {
        List<Investment> investments = investmentRepository.findAllByUserId(userId);

        if (investments.isEmpty()) {
            logger.warn("No investments found for user ID: {}", userId);
        }

        return investments.stream()
                .map(this::createInvestmentResponse)
                .collect(Collectors.toList());
    }

    public InvestmentSummaryResponse getUserInvestmentSummary(Long userId) {
        List<Investment> investments = investmentRepository.findAllByUserId(userId);
        logger.info("Found {} investments for user ID: {}", investments.size(), userId);
    
        if (investments.isEmpty()) {
            logger.warn("No investments available for user ID: {}", userId);
            return new InvestmentSummaryResponse(BigDecimal.ZERO, BigDecimal.ZERO, 0);
        }
    
        return calculateInvestmentSummary(investments);
    }
    
    public InvestmentResponse createInvestmentResponse(Investment investment) {
        InvestmentSummaryResponse summary = calculateInvestmentSummary(List.of(investment));
        return new InvestmentResponse(investment.getId(), investment.getName(), summary.getTotalValue(), summary.getProfitability());
    }
    
    private InvestmentSummaryResponse calculateInvestmentSummary(List<Investment> investments) {
        BigDecimal totalValue = BigDecimal.ZERO;
        List<CashFlowData> cashFlowData = new ArrayList<>();
    
        for (Investment investment : investments) {
            totalValue = totalValue.add(calculateTotalValue(investment));
            cashFlowData.addAll(collectCashFlowData(investment));
        }
    
        cashFlowData = filterAndSortCashFlowData(cashFlowData);
        BigDecimal totalXirr = cashFlowData.isEmpty() ? BigDecimal.ZERO : calculateXirr(extractDates(cashFlowData), extractCashFlows(cashFlowData));
        BigDecimal profitability = totalXirr.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    
        return new InvestmentSummaryResponse(totalValue, profitability, investments.size());
    }
    
    private BigDecimal calculateTotalValue(Investment investment) {
        return investment.getCurrentPrice().multiply(BigDecimal.valueOf(investment.getCurrentQuantity()));
    }
    
    
    private List<CashFlowData> collectCashFlowData(Investment investment) {
        List<CashFlowData> cashFlowData = new ArrayList<>();

        investment.getTransactions().forEach(transaction -> 
            cashFlowData.add(new CashFlowData(transactionService.calculateCashFlow(transaction), transaction.getTimestamp())));

        investment.getDividends().forEach(dividend -> 
            cashFlowData.add(new CashFlowData(dividend.getAmount(), dividend.getTimestamp())));

        BigDecimal currentValue = investment.getCurrentPrice()
                .multiply(BigDecimal.valueOf(investment.getCurrentQuantity()));
        cashFlowData.add(new CashFlowData(currentValue, Instant.now()));

        return cashFlowData;
    }

    private List<CashFlowData> filterAndSortCashFlowData(List<CashFlowData> cashFlowData) {
        return cashFlowData.stream()
                .filter(cashFlow -> cashFlow.getDate().isBefore(Instant.now())
                        || cashFlow.getDate().equals(Instant.now())) // filter out future dates
                .filter(cashFlow -> cashFlow.getAmount().compareTo(BigDecimal.ZERO) != 0) // filter out zero amounts
                .sorted(Comparator.comparing(CashFlowData::getDate)) // sort by date
                .collect(Collectors.toList());
    }

    private static List<Instant> extractDates(List<CashFlowData> cashFlowDataList) {
        return cashFlowDataList.stream()
                .map(CashFlowData::getDate)
                .collect(Collectors.toList());
    }

    private static List<BigDecimal> extractCashFlows(List<CashFlowData> cashFlowDataList) {
        return cashFlowDataList.stream()
                .map(CashFlowData::getAmount)
                .collect(Collectors.toList());
    }
}

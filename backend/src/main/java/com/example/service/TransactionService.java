package com.example.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.enums.TransactionType;
import com.example.model.Transaction;

@Service
public class TransactionService {

        public BigDecimal calculateCashFlow(Transaction transaction) {
        BigDecimal price = transaction.getPrice();
        BigDecimal quantity = BigDecimal.valueOf(transaction.getQuantity());
        BigDecimal fee = transaction.getFee();

        return transaction.getType() == TransactionType.BUY
                ? price.multiply(quantity.negate()).subtract(fee)
                : price.multiply(quantity).subtract(fee);
    }

}

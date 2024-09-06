package com.egdk.invoicesystem.exception;

import java.math.BigDecimal;

public class InvalidPaymentAmountException extends RuntimeException {
    public InvalidPaymentAmountException(BigDecimal amount) {
        super("Invalid payment amount: " + amount);
    }
}

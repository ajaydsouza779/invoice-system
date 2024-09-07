package com.egdk.invoicesystem.exception;

import java.math.BigDecimal;

import static com.egdk.invoicesystem.constants.Messages.INVALID_PAYMENT_AMOUNT;

public class InvalidPaymentAmountException extends RuntimeException {
    public InvalidPaymentAmountException(BigDecimal amount) {
        super(INVALID_PAYMENT_AMOUNT + amount);
    }
}

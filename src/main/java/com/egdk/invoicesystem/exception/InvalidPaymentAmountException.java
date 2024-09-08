package com.egdk.invoicesystem.exception;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import static com.egdk.invoicesystem.constants.Messages.INVALID_PAYMENT_AMOUNT;

@Slf4j
public class InvalidPaymentAmountException extends RuntimeException {
    public InvalidPaymentAmountException(BigDecimal amount) {
        super(INVALID_PAYMENT_AMOUNT + amount);
        log.error(INVALID_PAYMENT_AMOUNT+" {}", amount);
    }
}

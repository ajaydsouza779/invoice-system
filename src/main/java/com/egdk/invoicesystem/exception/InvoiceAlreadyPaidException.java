package com.egdk.invoicesystem.exception;


import lombok.extern.slf4j.Slf4j;

import static com.egdk.invoicesystem.constants.Messages.INVOICE_ALREADY_PAID;

@Slf4j
public class InvoiceAlreadyPaidException extends RuntimeException {
    public InvoiceAlreadyPaidException(Long id) {
        super(INVOICE_ALREADY_PAID);
        log.error("Invoice with id {} is already marked paid in previous transactions", id);
    }
}
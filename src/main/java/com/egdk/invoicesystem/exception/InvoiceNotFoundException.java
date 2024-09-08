package com.egdk.invoicesystem.exception;

import lombok.extern.slf4j.Slf4j;

import static com.egdk.invoicesystem.constants.Messages.INVOICE_NOT_FOUND;

@Slf4j
public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long id) {
        super(INVOICE_NOT_FOUND + id);
        log.error(INVOICE_NOT_FOUND+" {}", id);
    }
}
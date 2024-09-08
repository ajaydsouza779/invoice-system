package com.egdk.invoicesystem.exception;


import lombok.extern.slf4j.Slf4j;

import static com.egdk.invoicesystem.constants.Messages.INVOICE_VOID;


@Slf4j
public class InvoiceVoidException extends RuntimeException {
    public InvoiceVoidException(Long id) {
        super(INVOICE_VOID );
        log.error("Invoice with id {} is already void", id);
    }
}
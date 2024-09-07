package com.egdk.invoicesystem.exception;

import static com.egdk.invoicesystem.constants.Messages.INVOICE_NOT_FOUND;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long id) {
        super(INVOICE_NOT_FOUND + id);
    }
}
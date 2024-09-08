package com.egdk.invoicesystem.exception;


import static com.egdk.invoicesystem.constants.Messages.INVOICE_ALREADY_PAID;

public class InvoiceAlreadyPaidException extends RuntimeException {
    public InvoiceAlreadyPaidException() {
        super(INVOICE_ALREADY_PAID);
    }
}
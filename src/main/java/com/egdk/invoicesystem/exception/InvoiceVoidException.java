package com.egdk.invoicesystem.exception;


import static com.egdk.invoicesystem.constants.Messages.INVOICE_VOID;

public class InvoiceVoidException extends RuntimeException {
    public InvoiceVoidException() {
        super(INVOICE_VOID );
    }
}
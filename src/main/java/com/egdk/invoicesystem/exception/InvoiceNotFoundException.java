package com.egdk.invoicesystem.exception;

public class InvoiceNotFoundException extends RuntimeException {
    public InvoiceNotFoundException(Long id) {
        super("Invoice not found with id: " + id);
    }
}
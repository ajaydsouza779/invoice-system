package com.egdk.invoicesystem.constants;

public class Messages {
    // Error messages
    public static final String INVALID_PAYMENT_AMOUNT = "Invalid payment amount: ";
    public static final String INVOICE_NOT_FOUND = "Invoice not found with id:";
    public static final String INVOICE_VOID = "Invoice is already void. ";
    public static final String INVOICE_ALREADY_PAID = "Invoice is already fully paid. ";

    // Validation messages
    public static final String INVALID_DATE_FORMAT = "Due date must be in the format YYYY-MM-DD";
    public static final String NEGATIVE_AMOUNT = "Amount must be positive.";
    public static final String REQUIRED_AMOUNT = "Amount is required";
    public static final String REQUIRED_DATE = "Due date is required";

    public static final String INVALID_PAYMENT ="Invalid payment amount: ";

    // Other constants
    public static final String SUCCESSFUL_PAYMENT = "Payment processed successfully.";
}

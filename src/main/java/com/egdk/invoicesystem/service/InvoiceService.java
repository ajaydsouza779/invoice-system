package com.egdk.invoicesystem.service;

import com.egdk.invoicesystem.model.entity.Invoice;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InvoiceService {

    Invoice getInvoiceById(Long id);

    List<Invoice> getAllInvoices();

    Invoice createInvoice(BigDecimal amount, LocalDate dueDate);

    void processPayment(Long id, BigDecimal paymentAmount);

    void processOverdueInvoices(BigDecimal lateFee, int overdueDays);
}

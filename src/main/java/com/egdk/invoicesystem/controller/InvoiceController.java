package com.egdk.invoicesystem.controller;

import com.egdk.invoicesystem.exception.InvalidPaymentAmountException;
import com.egdk.invoicesystem.exception.InvoiceNotFoundException;
import com.egdk.invoicesystem.model.dto.InvoiceRequest;
import com.egdk.invoicesystem.model.dto.OverdueRequest;
import com.egdk.invoicesystem.model.dto.PaymentRequest;
import com.egdk.invoicesystem.model.entity.Invoice;
import com.egdk.invoicesystem.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
    public Invoice getInvoiceById(@PathVariable Long id) {
        return invoiceService.getInvoiceById(id);
    }

    @PostMapping
    public Invoice createInvoice(@RequestBody InvoiceRequest request) {
//        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
//            throw new InvalidPaymentAmountException(request.getAmount());
//        } TODO
        return invoiceService.createInvoice(request.getAmount(), request.getDueDate());
    }

    @PostMapping("/{id}/payments")
    public void processPayment(@PathVariable Long id, @RequestBody PaymentRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidPaymentAmountException(request.getAmount());
        }
        invoiceService.processPayment(id, request.getAmount());
    }

    @PostMapping("/process-overdue")
    public void processOverdueInvoices(@RequestBody OverdueRequest request) {
        invoiceService.processOverdueInvoices(request.getLateFee(), request.getOverdueDays());
    }
}

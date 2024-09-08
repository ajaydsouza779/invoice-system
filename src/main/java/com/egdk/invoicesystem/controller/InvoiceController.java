package com.egdk.invoicesystem.controller;

import com.egdk.invoicesystem.exception.InvoiceNotFoundException;
import com.egdk.invoicesystem.model.dto.InvoiceRequest;
import com.egdk.invoicesystem.model.dto.OverdueRequest;
import com.egdk.invoicesystem.model.dto.PaymentRequest;
import com.egdk.invoicesystem.model.entity.Invoice;
import com.egdk.invoicesystem.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static com.egdk.invoicesystem.constants.Messages.INVALID_PAYMENT;
import static com.egdk.invoicesystem.constants.Messages.SUCCESSFUL_PAYMENT;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {

        List<Invoice> invoices = invoiceService.getAllInvoices();
        if (invoices.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(invoices);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {

        Invoice invoice = invoiceService.getInvoiceById(id);
        return ResponseEntity.ok(invoice);

    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        Invoice createdInvoice = invoiceService.createInvoice(request.getAmount(), request.getDueDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<String> processPayment(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(INVALID_PAYMENT + request.getAmount());
        }

        invoiceService.processPayment(id, request.getAmount());
        return ResponseEntity.ok(SUCCESSFUL_PAYMENT); // Successful payment processing

    }

    @PostMapping("/process-overdue")
    public ResponseEntity<Void> processOverdueInvoices(@Valid @RequestBody OverdueRequest request) {
        invoiceService.processOverdueInvoices(request.getLateFee(), request.getOverdueDays());
        return ResponseEntity.noContent().build(); // Returns a 204 No Content status
    }


}

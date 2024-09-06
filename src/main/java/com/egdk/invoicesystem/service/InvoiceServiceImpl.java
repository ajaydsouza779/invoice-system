package com.egdk.invoicesystem.service;

import com.egdk.invoicesystem.exception.InvoiceNotFoundException;
import com.egdk.invoicesystem.model.entity.Invoice;
import com.egdk.invoicesystem.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new InvoiceNotFoundException(id));
    }

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public Invoice createInvoice(BigDecimal amount, LocalDate dueDate) {
        Invoice invoice = new Invoice();
        invoice.setAmount(amount);
        invoice.setDueDate(dueDate);
        return invoiceRepository.save(invoice);
    }

    @Override
    public void processPayment(Long id, BigDecimal paymentAmount) {

        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException(id));
        invoice.setPaidAmount(invoice.getPaidAmount().add(paymentAmount));
        if (invoice.getPaidAmount().compareTo(invoice.getAmount()) >= 0) {
            invoice.setStatus("paid");
        }
        invoiceRepository.save(invoice);
    }

    @Override
    public void processOverdueInvoices(BigDecimal lateFee, int overdueDays) {
        LocalDate now = LocalDate.now();
        List<Invoice> invoices = invoiceRepository.findAll();
        for (Invoice invoice : invoices) {
            if ("pending".equals(invoice.getStatus()) && invoice.getDueDate().isBefore(now.minusDays(overdueDays))) {
                BigDecimal remainingAmount = invoice.getAmount().subtract(invoice.getPaidAmount());
                if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
                    invoice.setStatus("void");
                    invoiceRepository.save(invoice);

                    Invoice newInvoice = new Invoice();
                    newInvoice.setAmount(remainingAmount.add(lateFee));
                    newInvoice.setDueDate(now.plusDays(overdueDays));
                    newInvoice.setStatus("pending");
                    invoiceRepository.save(newInvoice);
                } else {
                    invoice.setStatus("paid");
                    invoiceRepository.save(invoice);
                }
            }
        }
    }


}

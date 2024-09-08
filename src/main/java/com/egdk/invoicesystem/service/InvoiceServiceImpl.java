package com.egdk.invoicesystem.service;

import com.egdk.invoicesystem.exception.InvoiceAlreadyPaidException;
import com.egdk.invoicesystem.exception.InvoiceNotFoundException;
import com.egdk.invoicesystem.exception.InvoiceVoidException;
import com.egdk.invoicesystem.model.InvoiceStatus;
import com.egdk.invoicesystem.model.entity.Invoice;
import com.egdk.invoicesystem.repository.InvoiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public Invoice getInvoiceById(Long id) {
        log.debug("Retrieving invoice with ID: {}", id);
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

        invoice = invoiceRepository.save(invoice);
        log.info("Creating invoice: {}", invoice.getId());
        log.debug("Invoice details: {}", invoice);
        return invoice;
    }

    @Override
    public void processPayment(Long id, BigDecimal paymentAmount) {

        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException(id));
        log.debug("Invoice found with id-{}", id);

        if(InvoiceStatus.VOID.equals( invoice.getStatus()))
            throw new InvoiceVoidException(id);

        if(InvoiceStatus.PAID.equals(invoice.getStatus()))
            throw new InvoiceAlreadyPaidException(id);

        invoice.setPaidAmount(invoice.getPaidAmount().add(paymentAmount));
        if (invoice.getPaidAmount().compareTo(invoice.getAmount()) >= 0) {
            log.debug("Invoice {} marked as paid", id);
            invoice.setStatus(InvoiceStatus.PAID);
        }
        invoiceRepository.save(invoice);
    }

    @Override
    public void processOverdueInvoices(BigDecimal lateFee, int overdueDays) {
        LocalDate now = LocalDate.now();
        List<Invoice> invoices = invoiceRepository.findAll();
        for (Invoice invoice : invoices) {
            if (InvoiceStatus.PENDING.equals(invoice.getStatus()) && invoice.getDueDate().isBefore(now.minusDays(overdueDays))) {
                BigDecimal remainingAmount = invoice.getAmount().subtract(invoice.getPaidAmount());
                if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
                    invoice.setStatus(InvoiceStatus.VOID);
                    log.debug("Invoice {} marked as void" , invoice.getId());
                    invoiceRepository.save(invoice);

                    Invoice newInvoice = new Invoice();
                    newInvoice.setAmount(remainingAmount.add(lateFee));
                    newInvoice.setDueDate(now.plusDays(overdueDays));
                    newInvoice.setStatus(InvoiceStatus.PENDING);
                    newInvoice =  invoiceRepository.save(newInvoice);
                    log.debug("Invoice {} is  created for invoice id - {}", newInvoice.getId(), invoice.getId());

                } else {
                    invoice.setStatus(InvoiceStatus.PENDING);
                    invoiceRepository.save(invoice);
                }
            }
        }
    }


}

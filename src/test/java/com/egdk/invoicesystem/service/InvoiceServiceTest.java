package com.egdk.invoicesystem.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.egdk.invoicesystem.model.dto.InvoiceRequest;
import com.egdk.invoicesystem.model.entity.Invoice;
import com.egdk.invoicesystem.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllInvoices() {
        Invoice invoice = new Invoice();
        invoice.setId(Long.valueOf("1234"));
        invoice.setAmount(BigDecimal.valueOf(199.99));
        invoice.setDueDate(LocalDate.of(2024, 9, 11));
        invoice.setStatus("pending");

        when(invoiceRepository.findAll()).thenReturn(List.of(invoice));

        List<Invoice> invoices = invoiceService.getAllInvoices();
        assertEquals(1, invoices.size());
        assertEquals(Long.valueOf("1234"), invoices.get(0).getId());
    }

    @Test
    public void testCreateInvoice() {
        Invoice invoice = new Invoice();
        invoice.setId(Long.valueOf("1234"));
        invoice.setAmount(BigDecimal.valueOf(199.99));
        invoice.setDueDate(LocalDate.of(2024, 9, 11));
        invoice.setStatus("pending");

        when(invoiceRepository.save(any())).thenReturn(invoice);

        Invoice createdInvoice = invoiceService.createInvoice(invoice.getAmount(), invoice.getDueDate());
        assertNotNull(createdInvoice.getId());
    }

}


package com.egdk.invoicesystem.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.egdk.invoicesystem.exception.InvoiceAlreadyPaidException;
import com.egdk.invoicesystem.exception.InvoiceNotFoundException;
import com.egdk.invoicesystem.exception.InvoiceVoidException;
import com.egdk.invoicesystem.model.InvoiceStatus;
import com.egdk.invoicesystem.model.entity.Invoice;
import com.egdk.invoicesystem.repository.InvoiceRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

public class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private InvoiceServiceImpl invoiceService;

    @Mock
    private Logger log;

    private AutoCloseable mocks;

    @BeforeEach
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void testGetAllInvoices() {
        Invoice invoice = new Invoice();
        invoice.setId(Long.valueOf("1234"));
        invoice.setAmount(BigDecimal.valueOf(199.99));
        invoice.setDueDate(LocalDate.of(2024, 9, 11));
        invoice.setStatus(InvoiceStatus.PENDING);

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
        invoice.setStatus(InvoiceStatus.PENDING);

        when(invoiceRepository.save(any())).thenReturn(invoice);

        Invoice createdInvoice = invoiceService.createInvoice(invoice.getAmount(), invoice.getDueDate());
        assertNotNull(createdInvoice.getId());
    }

    @Test
    public void testProcessPayment_Success() {
        
        Long invoiceId = 1L;
        BigDecimal paymentAmount = new BigDecimal("100.00");

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setAmount(new BigDecimal("200.00"));
        invoice.setPaidAmount(new BigDecimal("50.00"));
        invoice.setStatus(InvoiceStatus.PENDING);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        
        invoiceService.processPayment(invoiceId, paymentAmount);

        
        verify(invoiceRepository).save(invoice);

        assertEquals(new BigDecimal("150.00"), invoice.getPaidAmount());
    }

    @Test
    public void testProcessPayment_InvoiceNotFound() {
        
        Long invoiceId = 1L;
        BigDecimal paymentAmount = new BigDecimal("100.00");

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        
        assertThrows(InvoiceNotFoundException.class, () -> invoiceService.processPayment(invoiceId, paymentAmount));
    }

    @Test
    public void testProcessPayment_InvoiceVoid() {
        
        Long invoiceId = 1L;
        BigDecimal paymentAmount = new BigDecimal("100.00");

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setAmount(new BigDecimal("200.00"));
        invoice.setPaidAmount(new BigDecimal("50.00"));
        invoice.setStatus(InvoiceStatus.VOID);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        
        assertThrows(InvoiceVoidException.class, () -> invoiceService.processPayment(invoiceId, paymentAmount));
    }

    @Test
    public void testProcessPayment_InvoiceAlreadyPaid() {
        
        Long invoiceId = 1L;
        BigDecimal paymentAmount = new BigDecimal("100.00");

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setAmount(new BigDecimal("200.00"));
        invoice.setPaidAmount(new BigDecimal("200.00"));
        invoice.setStatus(InvoiceStatus.PAID);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        assertThrows(InvoiceAlreadyPaidException.class, () -> invoiceService.processPayment(invoiceId, paymentAmount));
    }

    @Test
    public void testProcessPayment_InvoicePaidInFull() {
        
        Long invoiceId = 1L;
        BigDecimal paymentAmount = new BigDecimal("200.00");

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setAmount(new BigDecimal("200.00"));
        invoice.setPaidAmount(new BigDecimal("0.00"));
        invoice.setStatus(InvoiceStatus.PENDING);

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        
        invoiceService.processPayment(invoiceId, paymentAmount);

        
        verify(invoiceRepository).save(invoice);
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        assertEquals(new BigDecimal("200.00"), invoice.getPaidAmount());
    }

    @Test
    public void testProcessOverdueInvoices_WithPendingOverdueInvoice() {
        
        BigDecimal lateFee = new BigDecimal("50.00");
        int overdueDays = 30;
        LocalDate now = LocalDate.now();

        Invoice overdueInvoice = new Invoice();
        overdueInvoice.setId(1L);
        overdueInvoice.setAmount(new BigDecimal("300.00"));
        overdueInvoice.setPaidAmount(new BigDecimal("100.00"));
        overdueInvoice.setDueDate(now.minusDays(40));
        overdueInvoice.setStatus(InvoiceStatus.PENDING);

        List<Invoice> invoices = new ArrayList<>();
        invoices.add(overdueInvoice);

        when(invoiceRepository.findAll()).thenReturn(invoices);
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        
        invoiceService.processOverdueInvoices(lateFee, overdueDays);

        
        verify(invoiceRepository, times(2)).save(any(Invoice.class)); // Existing invoice and new one
        assertEquals(InvoiceStatus.VOID, overdueInvoice.getStatus()); // Check if original invoice is VOID

        // Check that the new invoice was created with the correct amount and due date
        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository, times(2)).save(invoiceCaptor.capture());
        Invoice newInvoice = invoiceCaptor.getAllValues().get(1);

        assertEquals(overdueInvoice.getAmount().subtract(overdueInvoice.getPaidAmount()).add(lateFee), newInvoice.getAmount()); // Remaining amount + late fee
        assertEquals(InvoiceStatus.PENDING, newInvoice.getStatus());
        assertEquals(now.plusDays(overdueDays), newInvoice.getDueDate()); // Due date updated
    }

    @Test
    public void testProcessOverdueInvoices_WithNoPendingInvoices() {
        
        BigDecimal lateFee = new BigDecimal("50.00");
        int overdueDays = 30;
        List<Invoice> invoices = new ArrayList<>(); // No overdue invoices

        when(invoiceRepository.findAll()).thenReturn(invoices);

        
        invoiceService.processOverdueInvoices(lateFee, overdueDays);

        
        verify(invoiceRepository, never()).save(any(Invoice.class)); // No invoices should be saved
    }

    @Test
    public void testProcessOverdueInvoices_WithNonOverduePendingInvoice() {
        
        BigDecimal lateFee = new BigDecimal("50.00");
        int overdueDays = 30;
        LocalDate now = LocalDate.now();

        Invoice pendingInvoice = new Invoice();
        pendingInvoice.setId(1L);
        pendingInvoice.setAmount(new BigDecimal("300.00"));
        pendingInvoice.setPaidAmount(new BigDecimal("100.00"));
        pendingInvoice.setDueDate(now.minusDays(20)); // Not overdue
        pendingInvoice.setStatus(InvoiceStatus.PENDING);

        List<Invoice> invoices = new ArrayList<>();
        invoices.add(pendingInvoice);

        when(invoiceRepository.findAll()).thenReturn(invoices);

        
        invoiceService.processOverdueInvoices(lateFee, overdueDays);

        
        verify(invoiceRepository, never()).save(any(Invoice.class)); // No invoices should be saved as it's not overdue
    }

    @Test
    public void testProcessOverdueInvoices_WithFullyPaidInvoice() {
        
        BigDecimal lateFee = new BigDecimal("50.00");
        int overdueDays = 30;
        LocalDate now = LocalDate.now();

        Invoice fullyPaidInvoice = new Invoice();
        fullyPaidInvoice.setId(1L);
        fullyPaidInvoice.setAmount(new BigDecimal("300.00"));
        fullyPaidInvoice.setPaidAmount(new BigDecimal("300.00"));
        fullyPaidInvoice.setDueDate(now.minusDays(40)); // Overdue, but fully paid
        fullyPaidInvoice.setStatus(InvoiceStatus.PENDING);

        List<Invoice> invoices = new ArrayList<>();
        invoices.add(fullyPaidInvoice);

        when(invoiceRepository.findAll()).thenReturn(invoices);

        
        invoiceService.processOverdueInvoices(lateFee, overdueDays);

        
        verify(invoiceRepository, times(1)).save(fullyPaidInvoice); // Only the existing invoice should be saved
        assertEquals(InvoiceStatus.PENDING, fullyPaidInvoice.getStatus()); // Status remains pending, no new invoice created
    }

    @Test
    public void testGetInvoiceById_Success() {
        
        Long invoiceId = 1L;
        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));

        
        Invoice result = invoiceService.getInvoiceById(invoiceId);

        
        assertNotNull(result); // Ensure an invoice is returned
        assertEquals(invoiceId, result.getId()); // Ensure the correct invoice is returned
        verify(invoiceRepository, times(1)).findById(invoiceId); // Ensure the repository is called exactly once
    }

    @Test
    public void testGetInvoiceById_InvoiceNotFound() {
        
        Long invoiceId = 1L;
        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty()); // Simulate no invoice found

        
        InvoiceNotFoundException exception = assertThrows(InvoiceNotFoundException.class, () -> invoiceService.getInvoiceById(invoiceId));

        // Verify the exception contains the correct message
        assertEquals("Invoice not found with id:" + invoiceId, exception.getMessage());
        verify(invoiceRepository, times(1)).findById(invoiceId); // Ensure the repository is called exactly once
    }
}


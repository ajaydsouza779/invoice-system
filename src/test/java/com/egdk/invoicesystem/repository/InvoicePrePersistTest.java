package com.egdk.invoicesystem.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import com.egdk.invoicesystem.model.InvoiceStatus;
import com.egdk.invoicesystem.model.entity.Invoice;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest // This will start an in-memory database for testing
public class InvoicePrePersistTest {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @Transactional
    public void testPrePersist_DefaultValues() {

        Invoice invoice = new Invoice();
        invoice.setAmount(new BigDecimal("100.00")); // Only set the amount

        //Save the invoice to trigger the @PrePersist method
        invoiceRepository.save(invoice);
        entityManager.flush(); // Force the JPA provider to trigger @PrePersist

        // Assert: Verify that paidAmount and status are set correctly by @PrePersist
        assertEquals(BigDecimal.ZERO, invoice.getPaidAmount());
        assertEquals(InvoiceStatus.PENDING, invoice.getStatus());
    }
}

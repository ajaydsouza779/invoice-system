package com.egdk.invoicesystem.controller;

import com.egdk.invoicesystem.model.InvoiceStatus;
import com.egdk.invoicesystem.model.dto.InvoiceRequest;
import com.egdk.invoicesystem.model.dto.OverdueRequest;
import com.egdk.invoicesystem.model.dto.PaymentRequest;
import com.egdk.invoicesystem.model.entity.Invoice;
import com.egdk.invoicesystem.repository.InvoiceRepository;
import com.egdk.invoicesystem.service.InvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class InvoiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @MockBean
    private InvoiceService invoiceService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    void testGetAllInvoicesIntegrationWithData() throws Exception {
//
//        Invoice invoice = new Invoice();
//        invoice.setAmount(new BigDecimal("100.00"));
//        invoice.setPaidAmount(new BigDecimal("0.00"));
//        invoice.setDueDate(LocalDate.now());
//        invoice.setStatus(InvoiceStatus.PENDING);
//        invoiceRepository.save(invoice);
//
//
//        mockMvc.perform(get("/invoices"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].amount").value(100.00))
//                .andExpect(jsonPath("$[0].status").value("PENDING"));
//    }

    @Test
    void testGetAllInvoicesIntegrationNoData() throws Exception {

        mockMvc.perform(get("/invoices")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testCreateInvoiceIntegration() throws Exception {
        InvoiceRequest request = new InvoiceRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDueDate(String.valueOf(LocalDate.of(2024, 9, 30)));

        Invoice createdInvoice = new Invoice();
        createdInvoice.setId(1L);
        createdInvoice.setAmount(new BigDecimal("100.00"));
        createdInvoice.setDueDate(LocalDate.of(2024, 9, 30));
        createdInvoice.setStatus(InvoiceStatus.PENDING);

        // Mock the service method
        when(invoiceService.createInvoice(any(BigDecimal.class), any(LocalDate.class)))
                .thenReturn(createdInvoice);

        // Perform the POST request and verify the response
        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.dueDate").value("2024-09-30"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
    @Test
    void testProcessPayment_Success() throws Exception {
        Long invoiceId = 1L;
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("50.00"));

        doNothing().when(invoiceService).processPayment(invoiceId, request.getAmount());

        mockMvc.perform(post("/invoices/{id}/payments", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Payment processed successfully."));
    }

    @Test
    void testProcessPayment_InvalidAmount() throws Exception {
        Long invoiceId = 1L;
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("-10.00"));

        mockMvc.perform(post("/invoices/{id}/payments", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.validationErrors.amount").value("Amount must be positive."));
    }

    @Test
    void testProcessOverdueInvoices_Success() throws Exception {
        OverdueRequest request = new OverdueRequest();
        request.setLateFee(new BigDecimal("10.00"));
        request.setOverdueDays(30);

        doNothing().when(invoiceService).processOverdueInvoices(request.getLateFee(), request.getOverdueDays());

        mockMvc.perform(post("/invoices/process-overdue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
    }
}

package com.egdk.invoicesystem.controller;
import com.egdk.invoicesystem.exception.InvoiceNotFoundException;
import com.egdk.invoicesystem.model.InvoiceStatus;
import com.egdk.invoicesystem.model.dto.InvoiceRequest;
import com.egdk.invoicesystem.model.dto.OverdueRequest;
import com.egdk.invoicesystem.model.dto.PaymentRequest;
import com.egdk.invoicesystem.model.entity.Invoice;
import com.egdk.invoicesystem.service.InvoiceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(InvoiceController.class)
public class InvoiceControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvoiceService invoiceService;

    @Test
    void testGetAllInvoices() throws Exception {
        // Mock service response
        List<Invoice> invoices = Arrays.asList(
                new Invoice(1L, new BigDecimal("100.00"), new BigDecimal("0.00"), LocalDate.now(), InvoiceStatus.PENDING)
        );
        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        // Perform request and verify response
        mockMvc.perform(MockMvcRequestBuilders.get("/invoices"))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 200 && status != 204) {
                        throw new AssertionError("Expected status 200 or 204 but was " + status);
                    }
                });
    }

    @Test
    void testGetAllInvoicesWithContent() throws Exception {
        List<Invoice> invoices = Arrays.asList(
                new Invoice(1L, new BigDecimal("100.00"), new BigDecimal("0.00"), LocalDate.now(), InvoiceStatus.PENDING)
        );
        when(invoiceService.getAllInvoices()).thenReturn(invoices);

        mockMvc.perform(MockMvcRequestBuilders.get("/invoices"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"amount\":100.00,\"paidAmount\":0.00,\"dueDate\":\"2024-09-09\",\"status\":\"PENDING\"}]"));
    }

    @Test
    void testGetAllInvoicesNoContent() throws Exception {
        when(invoiceService.getAllInvoices()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/invoices"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetInvoiceById() throws Exception {
        Invoice invoice = new Invoice(1L, new BigDecimal("100.00"), new BigDecimal("0.00"), LocalDate.now(), InvoiceStatus.PENDING);
        when(invoiceService.getInvoiceById(1L)).thenReturn(invoice);

        mockMvc.perform(get("/invoices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreateInvoice() throws Exception {
        InvoiceRequest request = new InvoiceRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setDueDate(String.valueOf(LocalDate.of(2024, 9, 30)));

        Invoice createdInvoice = new Invoice();
        createdInvoice.setId(1L);
        createdInvoice.setAmount(new BigDecimal("100.00"));
        createdInvoice.setDueDate(LocalDate.of(2024, 9, 30));
        createdInvoice.setStatus(InvoiceStatus.PENDING);

        when(invoiceService.createInvoice(any(BigDecimal.class), any(LocalDate.class))).thenReturn(createdInvoice);

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
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
                .andExpect(content().string("Payment processed successfully."));
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

        @Test
    public void testCreateInvoiceWithInvalidData() throws Exception {
        String invoiceJson = "{\"amount\":199.99,\"due_date\":\"2024-30-30\"}";

        MvcResult result =   mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoiceJson))
                .andExpect(status().isBadRequest()).andReturn();
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("Invalid date format or value"));    }

    @Test
    void testGetInvoiceNotFoundException() throws Exception {
        Long invalidId = 999L;

       when(invoiceService.getInvoiceById(invalidId)).thenThrow(new InvoiceNotFoundException(invalidId));


        MvcResult result =  mockMvc.perform(get("/invoices/{id}", invalidId))
                .andExpect(status().isNotFound()).andReturn();


        String responseContent = result.getResponse().getContentAsString();


        assertTrue(responseContent.contains("Invoice not found with id"));
        assertTrue(responseContent.contains("\"status\":404"));
    }

    @Test
    public void testNonNumericAmountInInvoice() throws Exception {
        String invoiceJson = "{\"amount\":\"-1\",\"due_date\":\"2024-09-30\"}";

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoiceJson))
                .andExpect(status().isBadRequest());
    }
        @Test
    public void testProcessOverdueInvoicesValidationError() throws Exception {
        OverdueRequest request = new OverdueRequest();
        request.setLateFee(new BigDecimal("-10.5"));  // Invalid late fee
        request.setOverdueDays(-5);  // Invalid overdue days

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/invoices/process-overdue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());  // Expect validation error
    }
}


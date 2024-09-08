package com.egdk.invoicesystem.controller;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.egdk.invoicesystem.exception.InvoiceNotFoundException;
import com.egdk.invoicesystem.model.InvoiceStatus;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

@WebMvcTest(InvoiceController.class)
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvoiceService invoiceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAllInvoices() throws Exception {

        Invoice invoice1 = new Invoice();
        invoice1.setId(1L);
        invoice1.setAmount(BigDecimal.valueOf(100.00));
        invoice1.setPaidAmount(BigDecimal.ZERO);
        invoice1.setDueDate(LocalDate.of(2024, 12, 31));
        invoice1.setStatus(InvoiceStatus.PENDING);

        Invoice invoice2 = new Invoice();
        invoice2.setId(2L);
        invoice2.setAmount(BigDecimal.valueOf(200.00));
        invoice2.setPaidAmount(BigDecimal.valueOf(50.00));
        invoice2.setDueDate(LocalDate.of(2024, 11, 30));
        invoice2.setStatus(InvoiceStatus.PAID);

        // Mocking the service layer to return the predefined list
        when(invoiceService.getAllInvoices()).thenReturn(Arrays.asList(invoice1, invoice2));


        mockMvc.perform(get("/invoices"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateInvoice() throws Exception {
        String invoiceJson = "{ \"amount\": 199.99, \"dueDate\": \"2024-09-11\" }";

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoiceJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testPayInvoice() throws Exception {
        String paymentJson = "{\"amount\":159.99}";

        mockMvc.perform(post("/invoices/1234/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(paymentJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateInvoiceWithInvalidAmount() throws Exception {
        String invoiceJson = "{ \"amount\": -100, \"due_date\": \"2024-09-11\" }";

        MvcResult result =   mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoiceJson))
                .andExpect(status().isBadRequest()).andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("Amount must be positive"));
    }

    @Test
    public void testCreateInvoiceWithInvalidData() throws Exception {
        String invoiceJson = "{\"amount\":199.99,\"due_date\":\"2024-30-30\"}";

        MvcResult result =   mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoiceJson))
                .andExpect(status().isBadRequest()).andReturn();
        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains("Due date is required"));    }

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
    public void testNonNumericAmount() throws Exception {
        String invoiceJson = "{\"amount\":\"-1\",\"due_date\":\"2024-09-30\"}";

        mockMvc.perform(post("/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invoiceJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testProcessOverdueInvoices() throws Exception {
        OverdueRequest request = new OverdueRequest();
        request.setLateFee(new BigDecimal("10.5"));
        request.setOverdueDays(10);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/invoices/process-overdue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNoContent());

        // Verify that the service method was called with correct arguments
        verify(invoiceService, times(1)).processOverdueInvoices(new BigDecimal("10.5"), 10);
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

    @Test
    public void testProcessPayment() throws Exception {
        Long invoiceId = 123L;
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("159.99"));

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/invoices/{id}/payments", invoiceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        // Verify that the service method was called with the correct arguments
        verify(invoiceService, times(1)).processPayment(invoiceId, new BigDecimal("159.99"));

    }

}
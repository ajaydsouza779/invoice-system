package com.egdk.invoicesystem.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InvoiceRequest {

    private BigDecimal amount;
    private LocalDate dueDate;
}

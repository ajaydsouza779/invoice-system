package com.egdk.invoicesystem.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OverdueRequest {
    private BigDecimal lateFee;
    private int overdueDays;
}

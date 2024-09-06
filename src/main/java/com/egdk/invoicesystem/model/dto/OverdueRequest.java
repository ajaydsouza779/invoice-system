package com.egdk.invoicesystem.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OverdueRequest {

    @NotNull
    @Min(0)
    private BigDecimal lateFee;

    @NotNull
    @Min(0)
    private Integer overdueDays;
}

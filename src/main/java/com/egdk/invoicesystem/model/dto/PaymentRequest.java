package com.egdk.invoicesystem.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import static com.egdk.invoicesystem.constants.Messages.NEGATIVE_AMOUNT;
import static com.egdk.invoicesystem.constants.Messages.REQUIRED_AMOUNT;

@Getter
@Setter
public class PaymentRequest {
    @NotNull(message = REQUIRED_AMOUNT)
    @DecimalMin(value = "0.0", inclusive = false, message = NEGATIVE_AMOUNT)
    private BigDecimal amount;
}

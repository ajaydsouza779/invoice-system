package com.egdk.invoicesystem.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter @AllArgsConstructor
public class InvoiceRequest {


    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Getter  @Setter
    private BigDecimal amount;

    @NotNull(message = "Due date is required")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Due date must be in the format YYYY-MM-DD")
    @Setter
    private String dueDate;



    public LocalDate getDueDate() {
        return LocalDate.parse(this.dueDate);  // Parse the string to LocalDate
    }
}

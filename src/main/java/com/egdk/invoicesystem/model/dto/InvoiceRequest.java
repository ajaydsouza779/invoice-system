package com.egdk.invoicesystem.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

import static com.egdk.invoicesystem.constants.Messages.*;

@AllArgsConstructor
public class InvoiceRequest {


    @NotNull(message = REQUIRED_AMOUNT)
    @Positive(message = NEGATIVE_AMOUNT)
    @Getter  @Setter
    private BigDecimal amount;

    @NotNull(message = REQUIRED_DATE)
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = INVALID_DATE_FORMAT)
    @Setter
    @JsonProperty("due_date")
    private String dueDate;


    public LocalDate getDueDate() {
        return LocalDate.parse(this.dueDate);  // Parse the string to LocalDate
    }
}

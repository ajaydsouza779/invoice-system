package com.egdk.invoicesystem.model.entity;

import com.egdk.invoicesystem.model.InvoiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @PrePersist
    public void prePersist() {
        if (this.paidAmount == null) {
            this.paidAmount = BigDecimal.ZERO;
        }
        if (this.status == null) {
            this.status = InvoiceStatus.PENDING;
        }
    }
}
package com.egdk.invoicesystem.model.entity;

import com.egdk.invoicesystem.model.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    public Invoice(Long id, BigDecimal amount, BigDecimal paidAmount, LocalDate dueDate, InvoiceStatus status) {
        this.id = id;
        this.amount = amount;
        this.paidAmount = paidAmount;
        this.dueDate = dueDate;
        this.status = status;
    }

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
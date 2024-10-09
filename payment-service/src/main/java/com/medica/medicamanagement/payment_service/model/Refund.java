package com.medica.medicamanagement.payment_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medica.dto.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "refunds")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "refund_transaction_id", unique = true)
    private String refundTransactionId;  // Refund Transaction ID from payment provider

    @Column(name = "refund_amount", nullable = false)
    private BigDecimal refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;  // REFUNDED, FAILED

    @Column(name = "reason", length = 500)
    private String reason;  // Reason for refund (optional)

    @Column(name = "created_at", nullable = false)
    private String createdAt;

    @Column(name = "updated_at")
    private String updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "transaction_id", nullable = false)
    private CustomTransaction customTransaction;
}

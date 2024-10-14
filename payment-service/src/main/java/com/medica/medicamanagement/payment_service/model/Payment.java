package com.medica.medicamanagement.payment_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.medica.dto.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "payments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID appointmentId;
    @Column(nullable = false)
    private String paymentMethod;
    @Column(nullable = false)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    @Column(name = "created_at", nullable = false)
    private String createdAt;
    @Column(name = "updated_at")
    private String updatedAt;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "payment")
    @JsonManagedReference
    private CustomTransaction customTransaction;
}

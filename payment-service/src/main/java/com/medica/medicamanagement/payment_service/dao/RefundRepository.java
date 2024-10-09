package com.medica.medicamanagement.payment_service.dao;

import com.medica.dto.PaymentStatus;
import com.medica.medicamanagement.payment_service.model.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RefundRepository extends JpaRepository<Refund, UUID> {
    List<Refund> findByStatus(PaymentStatus status);
}

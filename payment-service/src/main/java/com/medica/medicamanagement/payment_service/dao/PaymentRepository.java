package com.medica.medicamanagement.payment_service.dao;

import com.medica.medicamanagement.payment_service.model.CustomTransaction;
import com.medica.medicamanagement.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Payment findByCustomTransaction(CustomTransaction customTransaction);
}

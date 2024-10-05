package com.medica.medicamanagement.payment_service.dao;

import com.medica.medicamanagement.payment_service.model.CustomTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<CustomTransaction, UUID> {
    CustomTransaction findByTransactionId(String transactionId);
}


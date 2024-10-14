package com.medica.medicamanagement.payment_service.dao;

import com.medica.medicamanagement.payment_service.model.CustomTransaction;
import com.medica.medicamanagement.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * The interface Transaction repository.
 */
public interface TransactionRepository extends JpaRepository<CustomTransaction, UUID> {
    /**
     * Find by transaction id custom transaction.
     *
     * @param transactionId the transaction id
     * @return the custom transaction
     */
    CustomTransaction findByTransactionId(String transactionId);

    /**
     * Find by payment custom transaction.
     *
     * @param payment the payment
     * @return the custom transaction
     */
    CustomTransaction findByPayment(Payment payment);
}


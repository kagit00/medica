package com.medica.medicamanagement.payment_service.dao;

import com.medica.medicamanagement.payment_service.model.CustomTransaction;
import com.medica.medicamanagement.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

/**
 * The interface Payment repository.
 */
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    /**
     * Find by custom transaction payment.
     *
     * @param customTransaction the custom transaction
     * @return the payment
     */
    Payment findByCustomTransaction(CustomTransaction customTransaction);

    /**
     * Find by appointment id payment.
     *
     * @param appointmentId the appointment id
     * @return the payment
     */
    Payment findByAppointmentId(UUID appointmentId);
}

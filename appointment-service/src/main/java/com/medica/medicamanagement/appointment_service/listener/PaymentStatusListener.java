package com.medica.medicamanagement.appointment_service.listener;

import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.appointment_service.service.AppointmentProcessingService;
import com.medica.util.BasicUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentStatusListener {
    private final AppointmentProcessingService appointmentProcessingService;

    /**
     * Handle payment status.
     *
     * @param message the message
     */
    public void handlePaymentStatus(String message) {
        try {
            String appointmentId = BasicUtility.readSpecificProperty(message, "appointmentId");
            String status = BasicUtility.readSpecificProperty(message, "status");
            appointmentProcessingService.handlePaymentStatus(appointmentId, status);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error processing payment status: " + e.getMessage());
        }
    }
}

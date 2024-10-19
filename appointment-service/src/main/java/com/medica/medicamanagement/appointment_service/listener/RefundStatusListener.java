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
public class RefundStatusListener {
    private final AppointmentProcessingService appointmentProcessingService;

    /**
     * Handle refund status.
     *
     * @param response the response
     */
    public void handleRefundStatus(String response) {
        try {
            String appointmentId = BasicUtility.readSpecificProperty(response, "appointmentId");
            String status = BasicUtility.readSpecificProperty(response, "refundStatus");
            appointmentProcessingService.handleRefundStatus(appointmentId, status);
        } catch (Exception e) {
            throw new InternalServerErrorException("Error processing refund status: " + e.getMessage());
        }
    }
}

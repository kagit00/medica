package com.medica.medicamanagement.appointment_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.dto.AppointmentRequest;
import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.appointment_service.service.AppointmentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentRequestListener {
    private final ObjectMapper om;
    private final PubSubTemplate pubSubTemplate;
    private final AppointmentProcessingService appointmentProcessingService;

    /**
     * Handle appointment request.
     *
     * @param message the message
     */

    public void handleAppointmentRequest(String message) {
        try {
            AppointmentRequest request = om.readValue(message, AppointmentRequest.class);
            appointmentProcessingService.handleAppointmentScheduleRequest(request);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}

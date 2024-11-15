package com.medica.medicamanagement.appointment_service.handler;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type Appointment status update retry handler.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentStatusUpdateRetryHandler {
    private final AppointmentRepository appointmentRepository;
    private final PubSubTemplate pubSubTemplate;

    /**
     * Check pending appointments.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkPendingAppointments() {
        List<Appointment> pendingAppointments = appointmentRepository.findByStatus(AppointmentStatus.PENDING.name());

        int batchSize = 5;
        int processedCount = 0;

        for (Appointment appointment : pendingAppointments) {
            if (processedCount >= batchSize) {
                break;
            }

            pubSubTemplate.publish("appointment-status-update-retry", String.valueOf(appointment.getId()));
            processedCount++;
        }
    }
}

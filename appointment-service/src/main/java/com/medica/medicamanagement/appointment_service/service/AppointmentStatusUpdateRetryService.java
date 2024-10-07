package com.medica.medicamanagement.appointment_service.service;

import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentStatusUpdateRetryService {
    private final AppointmentRepository appointmentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkPendingAppointments() {
        List<Appointment> pendingAppointments = appointmentRepository.findByStatus(AppointmentStatus.PENDING.name());

        int batchSize = 5;
        int processedCount = 0;

        for (Appointment appointment : pendingAppointments) {
            if (processedCount >= batchSize) {
                break;
            }

            kafkaTemplate.send("appointment-status-update-retry", String.valueOf(appointment.getId()));
            processedCount++;
        }
    }
}

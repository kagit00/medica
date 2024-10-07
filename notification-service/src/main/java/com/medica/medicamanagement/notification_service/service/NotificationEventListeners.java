package com.medica.medicamanagement.notification_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationEventListeners {
    private final EmailService emailService;

    @KafkaListener(topics = "appointment-status-mail-for-patient", groupId = "notification-service-group")
    public void receiveAppointmentStatusAsPatient(String response) {
        try {
            emailService.sendEmailToPatient(response);
        } catch (Exception e) {
            log.error("Something went wrong {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "appointment-status-mail-for-doctor", groupId = "notification-service-group")
    public void receiveAppointmentStatusAsDoctor(String response) {
        try {
            emailService.sendEmailToDoctor(response);
        } catch (Exception e) {
            log.error("Something went wrong. {}", e.getMessage());
        }
    }
}

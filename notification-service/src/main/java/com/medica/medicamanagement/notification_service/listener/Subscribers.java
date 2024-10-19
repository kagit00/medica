package com.medica.medicamanagement.notification_service.listener;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class Subscribers {
    private final PubSubTemplate pubSubTemplate;
    private final AppointmentStatusNotificationListener appointmentStatusNotificationListener;
    private final UserManagementNotificationListener userManagementNotificationListener;

    @PostConstruct
    public void initializeSubscribers() {

        // Subscribe to the appointment status mail for patient topic
        pubSubTemplate.subscribe("appointment-status-mail-for-patient-subscription", message -> {
            String response = message.getPubsubMessage().getData().toStringUtf8();
            appointmentStatusNotificationListener.receiveAppointmentStatusAsPatient(response);
            message.ack();
        });

        // Subscribe to the appointment status mail for doctor topic
        pubSubTemplate.subscribe("appointment-status-mail-for-doctor-subscription", message -> {
            String response = message.getPubsubMessage().getData().toStringUtf8();
            appointmentStatusNotificationListener.receiveAppointmentStatusAsDoctor(response);
            message.ack();
        });


        pubSubTemplate.subscribe("user-password-changed-subscription", message -> {
            String response = message.getPubsubMessage().getData().toStringUtf8();
            userManagementNotificationListener.emailRegardingPasswordChange(response);
            message.ack();
        });
    }
}
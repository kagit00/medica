package com.medica.medicamanagement.doctor_service.listener;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class Subscribers {
    private final PubSubTemplate pubSubTemplate;
    private final AppointmentResponseListener appointmentResponseListener;
    private final AppointmentRetryListener appointmentRetryListener;
    private final AppointmentCancellationListener appointmentCancellationListener;
    private final AppointmentRescheduleListener appointmentRescheduleListener;

    @PostConstruct
    public void initializeSubscribers() {

        // Subscribe to the appointment response by appointment setters topic
        pubSubTemplate.subscribe("appointment-response-by-appointment-setters-subscription", message -> {
            String messageBody = message.getPubsubMessage().getData().toStringUtf8();
            appointmentResponseListener.respondToAppointmentRequest(messageBody);
            message.ack();
        });

        // Subscribe to the appointment status update retry topic
        pubSubTemplate.subscribe("appointment-status-update-retry-subscription", message -> {
            String appointmentId = message.getPubsubMessage().getData().toStringUtf8();
            appointmentRetryListener.handleAppointmentRetry(appointmentId);
            message.ack();
        });

        // Subscribe to the appointment cancellation by patient topic
        pubSubTemplate.subscribe("appointment-cancelled-by-patient-subscription", message -> {
            String appointmentId = message.getPubsubMessage().getData().toStringUtf8();
            appointmentCancellationListener.cancelAppointmentOnAppointmentSettersRequest(appointmentId);
            message.ack();
        });

        // Subscribe to the appointment rescheduled by appointment setters at patient request
        pubSubTemplate.subscribe("appointment-rescheduled-by-appointment-setters-at-patient-request-subscription", message -> {
            String response = message.getPubsubMessage().getData().toStringUtf8();
            appointmentRescheduleListener.handleAppointmentRescheduleAtPatientReq(response);
            message.ack();
        });
    }
}
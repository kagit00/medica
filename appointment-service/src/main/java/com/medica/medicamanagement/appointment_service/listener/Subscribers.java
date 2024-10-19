package com.medica.medicamanagement.appointment_service.listener;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class Subscribers {
    private final PubSubTemplate pubSubTemplate;
    private final DoctorResponseListener doctorResponseListener;
    private final PaymentStatusListener paymentStatusListener;
    private final RefundStatusListener refundStatusListener;
    private final AppointmentCancellationListener appointmentCancellationListener;
    private final AppointmentRescheduleListener appointmentRescheduleListener;
    private final AppointmentRequestListener appointmentRequestListener;

    @PostConstruct
    public void initializeSubscribers() {

        // Subscribe to the appointment request topic
        pubSubTemplate.subscribe("appointment-request-by-patient-subscription", message -> {
            String messageBody = message.getPubsubMessage().getData().toStringUtf8();
            appointmentRequestListener.handleAppointmentRequest(messageBody);
            message.ack();
        });

        // Subscribe to the appointment response by doctor topic
        pubSubTemplate.subscribe("appointment-response-by-doctor-subscription", message -> {
            String messageBody = message.getPubsubMessage().getData().toStringUtf8();
            doctorResponseListener.handleDoctorResponse(messageBody);
            message.ack();
        });

        // Subscribe to the appointment payment status topic
        pubSubTemplate.subscribe("appointment-payment-status-subscription", message -> {
            String messageBody = message.getPubsubMessage().getData().toStringUtf8();
            paymentStatusListener.handlePaymentStatus(messageBody);
            message.ack();
        });

        // Subscribe to the appointment refund status topic
        pubSubTemplate.subscribe("appointment-refund-status-subscription", message -> {
            String messageBody = message.getPubsubMessage().getData().toStringUtf8();
            refundStatusListener.handleRefundStatus(messageBody);
            message.ack();
        });

        // Subscribe to the appointment cancellation on patient request
        pubSubTemplate.subscribe("appointment-cancelled-by-patient-subscription", message -> {
            String appointmentId = message.getPubsubMessage().getData().toStringUtf8();
            appointmentCancellationListener.handleAppointmentCancellationOnPatientReq(appointmentId);
            message.ack();
        });

        // Subscribe to the appointment cancellation on doctor request
        pubSubTemplate.subscribe("appointment-cancelled-by-doctor-subscription", message -> {
            String appointmentId = message.getPubsubMessage().getData().toStringUtf8();
            appointmentCancellationListener.handleAppointmentCancellationOnDoctorReq(appointmentId);
            message.ack();
        });

        // Subscribe to the appointment reschedule at patient request
        pubSubTemplate.subscribe("appointment-rescheduled-by-patient-subscription", message -> {
            String response = message.getPubsubMessage().getData().toStringUtf8();
            appointmentRescheduleListener.rescheduleAppointmentAtPatientReq(response);
            message.ack();
        });
    }
}
package com.medica.medicamanagement.payment_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.AppointmentResponse;
import com.medica.medicamanagement.payment_service.dao.PaymentRepository;
import com.medica.medicamanagement.payment_service.handler.RefundHandler;
import com.medica.medicamanagement.payment_service.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentEventListenerService {
    private final RefundHandler refundHandler;
    private final ObjectMapper om;
    private final PaymentRepository paymentRepository;

    @KafkaListener(topics = "appointment-cancelled-by-doctor", groupId = "doctor-service-group")
    public void handleAppointmentRetry(String response) {
        try {
            AppointmentResponse appointmentResponse = om.readValue(response, AppointmentResponse.class);
            Payment payment = this.paymentRepository.findByAppointmentId(appointmentResponse.getId());
            this.refundHandler.refundPayment(payment);

        } catch (Exception e) {
            log.error("Something went wrong: {}", e.getMessage());
        }
    }
}

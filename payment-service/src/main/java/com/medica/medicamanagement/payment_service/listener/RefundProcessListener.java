package com.medica.medicamanagement.payment_service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.AppointmentResponse;
import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.payment_service.dao.PaymentRepository;
import com.medica.medicamanagement.payment_service.handler.RefundHandler;
import com.medica.medicamanagement.payment_service.model.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundProcessListener {

    private final ObjectMapper om;
    private final PaymentRepository paymentRepository;
    private final RefundHandler refundHandler;

    /**
     * Handle refund process for the appointment.
     *
     * @param response the response containing appointment details
     */
    public void handleRefundProcess(String response) {
        try {
            AppointmentResponse appointmentResponse = om.readValue(response, AppointmentResponse.class);
            Payment payment = this.paymentRepository.findByAppointmentId(appointmentResponse.getId());
            this.refundHandler.refundPayment(payment);
        } catch (Exception e) {
            throw new InternalServerErrorException("Something went wrong: " + e.getMessage());
        }
    }
}

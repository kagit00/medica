package com.medica.medicamanagement.appointment_service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentRescheduleRequest;
import com.medica.dto.DoctorApprovalResponse;
import com.medica.dto.DoctorResponse;
import com.medica.medicamanagement.appointment_service.service.AppointmentProcessingService;
import com.medica.util.BasicUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentEventHandler {
    private final ObjectMapper om;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AppointmentProcessingService appointmentProcessingService;

    @KafkaListener(topics = "appointment_request_by_patient", groupId = "appointment-service-group")
    public void handleAppointmentRequest(String message) {
        try {
            AppointmentRequest request = om.readValue(message, AppointmentRequest.class);
            appointmentProcessingService.handleAppointmentScheduleRequest(request);
        } catch (Exception e) {
            log.error("Error processing appointment request: {}", e.getMessage());
            kafkaTemplate.send("appointment_response_by_appointment_setters", e.getMessage());
        }
    }

    @KafkaListener(topics = "appointment_response_by_doctor", groupId = "appointment-service-group")
    public void handleDoctorResponse(String response) {
        try {
            String[] combinedValues = response.split(" <> ");
            DoctorApprovalResponse approvalResponse = om.readValue(combinedValues[0], DoctorApprovalResponse.class);
            DoctorResponse doctorResponse = om.readValue(combinedValues[1], DoctorResponse.class);
            appointmentProcessingService.handleDoctorResponse(approvalResponse, doctorResponse);
        } catch (Exception e) {
            log.error("Error processing doctor response: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "appointment-payment-status", groupId = "appointment-service-group")
    public void handlePaymentStatus(String message) {
        try {
            String appointmentId = BasicUtility.readSpecificProperty(message, "appointmentId");
            String status = BasicUtility.readSpecificProperty(message, "status");
            appointmentProcessingService.handlePaymentStatus(appointmentId, status);
        } catch (Exception e) {
            log.error("Error processing payment status: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "appointment-refund-status", groupId = "appointment-service-group")
    public void handleRefundStatus(String response) {
        try {
            String appointmentId = BasicUtility.readSpecificProperty(response, "appointmentId");
            String status = BasicUtility.readSpecificProperty(response, "refundStatus");
            appointmentProcessingService.handleRefundStatus(appointmentId, status);
        } catch (Exception e) {
            log.error("Error processing refund status: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "appointment-cancelled-by-patient", groupId = "appointment-service-group")
    public void handleAppointmentCancellationOnPatientReq(String appointmentId) {
        appointmentProcessingService.cancelAppointment(appointmentId, true);
    }

    @KafkaListener(topics = "appointment-cancelled-by-doctor", groupId = "appointment-service-group")
    public void handleAppointmentCancellationOnDoctorReq(String appointmentId) {
        appointmentProcessingService.cancelAppointment(appointmentId, false);
    }

    @KafkaListener(topics = "appointment-rescheduled-by-patient", groupId = "appointment-service-group")
    public void rescheduleAppointmentAtPatientReq(String response) {
        List<String> combinedValues = Arrays.asList(response.split(" <> "));
        String appointmentId = !combinedValues.isEmpty() ? combinedValues.get(0) : "";
        AppointmentRescheduleRequest appointmentRescheduleRequest = BasicUtility.deserializeJson(combinedValues, 1, AppointmentRescheduleRequest.class, om);
        this.appointmentProcessingService.rescheduleAppointment(appointmentId, appointmentRescheduleRequest);
    }
}

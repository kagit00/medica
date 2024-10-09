package com.medica.medicamanagement.doctor_service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.AppointmentResponse;
import com.medica.medicamanagement.doctor_service.dao.DoctorApprovalRepository;
import com.medica.medicamanagement.doctor_service.model.DoctorApproval;
import com.medica.medicamanagement.doctor_service.service.AppointmentProgressService;
import com.medica.medicamanagement.doctor_service.utils.DefaultValuesPopulator;
import com.medica.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentEventHandler {
    private final ObjectMapper om;
    private final DoctorApprovalRepository doctorApprovalRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AppointmentProgressService appointmentProgressService;
    private final AppointmentRescheduleHandler appointmentRescheduleHandler;

    @KafkaListener(topics = "appointment_response_by_appointment_setters", groupId = "doctor-service-group")
    public void respondToAppointmentRequest(String response) {
        try {
            AppointmentResponse appointmentResponse = om.readValue(response, AppointmentResponse.class);
            DoctorApproval existingApproval = this.doctorApprovalRepository.findByAppointmentId(appointmentResponse.getId());

            if (Objects.isNull(existingApproval)) {
                DoctorApproval approval = DoctorApproval.builder()
                        .doctorId(appointmentResponse.getDoctorId()).appointmentId(appointmentResponse.getId()).doctorComments("NA")
                        .status(AppointmentStatus.PENDING.name())
                        .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                        .build();

                doctorApprovalRepository.save(approval);
            } else {
                existingApproval.setStatus(appointmentResponse.getStatus());
                existingApproval.setDoctorComments(appointmentResponse.getStatus());
                existingApproval.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
                this.doctorApprovalRepository.save(existingApproval);
            }
        } catch (Exception e) {
            log.error("There is an issue with receiving appointment request. Reason: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "appointment-status-update-retry", groupId = "doctor-service-group")
    public void handleAppointmentRetry(String appointmentId) {
        this.appointmentProgressService.updateAppointmentStatus(UUID.fromString(appointmentId), AppointmentStatus.PENDING.name());
    }

    @KafkaListener(topics = "appointment-cancelled-by-patient", groupId = "doctor-service-group")
    public void cancelAppointmentOnAppointmentSettersRequest(String appointmentId) {
        DoctorApproval doctorApproval = this.doctorApprovalRepository.findByAppointmentId(UUID.fromString(appointmentId));
        if (AppointmentStatus.REJECTED.name().equals(doctorApproval.getStatus())) {
            return;
        }
        doctorApproval.setStatus(AppointmentStatus.CANCELED.name());
        this.doctorApprovalRepository.save(doctorApproval);
    }

    @KafkaListener(topics = "appointment-rescheduled-by-patient", groupId = "doctor-service-group")
    public void handleAppointmentRescheduleAtPatientReq(String response) {
        this.appointmentRescheduleHandler.handleRescheduleAppointmentAtPatientReq(response);
    }
}

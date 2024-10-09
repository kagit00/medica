package com.medica.medicamanagement.doctor_service.service;

import com.medica.dto.DoctorApprovalResponse;
import com.medica.medicamanagement.doctor_service.dao.DoctorApprovalRepository;
import com.medica.medicamanagement.doctor_service.model.DoctorApproval;
import com.medica.medicamanagement.doctor_service.utils.DefaultValuesPopulator;
import com.medica.medicamanagement.doctor_service.utils.ResponseMakerUtility;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentProgressServiceImplementation implements AppointmentProgressService {
    private final DoctorApprovalRepository doctorApprovalRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DoctorServiceImplementation doctorService;

    @Override
    public DoctorApprovalResponse updateAppointmentStatus(UUID appointmentId, String status) {
        DoctorApproval doctorApproval = this.doctorApprovalRepository.findByAppointmentId(appointmentId);

        updateAppointmentStatus(doctorApproval, status);
        this.doctorApprovalRepository.save(doctorApproval);

        DoctorApprovalResponse doctorApprovalResponse = ResponseMakerUtility.getDoctorApprovalResponse(doctorApproval);

        if (AppointmentStatus.CANCELED.name().equals(doctorApprovalResponse.getStatus())) {
            kafkaTemplate.send("appointment-cancelled-by-doctor", doctorApprovalResponse.getAppointmentId().toString());
        } else {
            kafkaTemplate.send("appointment_response_by_doctor", BasicUtility.stringifyObject(doctorApprovalResponse) + " <> " +
                    BasicUtility.stringifyObject(doctorService.getDoctorById(doctorApproval.getDoctorId()))
            );
        }
        return doctorApprovalResponse;
    }

    private void updateAppointmentStatus(DoctorApproval doctorApproval, String status) {
        switch (status) {
            case "APPROVED":
                doctorApproval.setDoctorComments("Appointment Approved");
                doctorApproval.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
                doctorApproval.setStatus(AppointmentStatus.APPROVED.name());
                break;

            case "REJECTED":
                doctorApproval.setDoctorComments("Appointment Rejected");
                doctorApproval.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
                doctorApproval.setStatus(AppointmentStatus.REJECTED.name());
                break;

            case "CANCELED":
                if (!AppointmentStatus.SCHEDULED.name().equals(doctorApproval.getStatus())) {
                    log.error("Appointment should be in scheduled state for cancellation");
                    return;
                }
                doctorApproval.setDoctorComments("Appointment Canceled");
                doctorApproval.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
                doctorApproval.setStatus(AppointmentStatus.CANCELED.name());
                break;

            default:
                doctorApproval.setDoctorComments("Appointment Pending");
                doctorApproval.setStatus(AppointmentStatus.PENDING.name());
                break;
        }
    }
}
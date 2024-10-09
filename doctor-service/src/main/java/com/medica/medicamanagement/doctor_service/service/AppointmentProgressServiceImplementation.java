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
        String currentStatus = doctorApproval.getStatus();
        switch (status) {
            case "APPROVED":
                if (AppointmentStatus.RESCHEDULE_REQUESTED.name().equals(currentStatus)) {
                    doctorApproval.setDoctorComments("Appointment Rescheduled");
                    doctorApproval.setStatus(AppointmentStatus.RESCHEDULED.name());
                } else if (AppointmentStatus.PENDING.name().equals(currentStatus) || AppointmentStatus.REVIEWED.name().equals(currentStatus)) {
                    doctorApproval.setDoctorComments("Appointment Approved");
                    doctorApproval.setStatus(AppointmentStatus.APPROVED.name());
                } else {
                    log.error("Unknown status request for approval .");
                    return;
                }
                break;

            case "REJECTED":
                if (AppointmentStatus.RESCHEDULED.name().equals(currentStatus) || AppointmentStatus.SCHEDULED.name().equals(currentStatus) || AppointmentStatus.APPROVED.name().equals(currentStatus)) {
                    log.error("Rescheduled or Scheduled or Approved appointments cannot be rejected.");
                    return;
                }
                doctorApproval.setDoctorComments("Appointment Rejected");
                doctorApproval.setStatus(AppointmentStatus.REJECTED.name());
                break;

            case "CANCELED":
                if (AppointmentStatus.SCHEDULED.name().equals(currentStatus) || AppointmentStatus.RESCHEDULED.name().equals(currentStatus)) {
                    doctorApproval.setDoctorComments("Appointment Canceled");
                    doctorApproval.setStatus(AppointmentStatus.CANCELED.name());
                } else {
                    log.error("Only Scheduled or Approved appointments can be canceled.");
                    return;
                }
                break;

            default:
                doctorApproval.setDoctorComments("Appointment Pending");
                doctorApproval.setStatus(AppointmentStatus.PENDING.name());
                break;
        }

        doctorApproval.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
        this.doctorApprovalRepository.save(doctorApproval);
    }
}

package com.medica.medicamanagement.doctor_service.service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.dto.DoctorApprovalResponse;
import com.medica.exception.BadRequestException;
import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.doctor_service.dao.DoctorApprovalRepository;
import com.medica.medicamanagement.doctor_service.model.DoctorApproval;
import com.medica.medicamanagement.doctor_service.utils.ResponseMakerUtility;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentProgressServiceImplementation implements AppointmentProgressService {
    private final DoctorApprovalRepository doctorApprovalRepository;
    private final PubSubTemplate pubSubTemplate;
    private final DoctorServiceImplementation doctorService;

    @Override
    public DoctorApprovalResponse updateAppointmentStatus(UUID appointmentId, String status) {

        DoctorApproval doctorApproval = this.doctorApprovalRepository.findByAppointmentId(appointmentId);
        updateAppointmentStatus(doctorApproval, status);
        DoctorApprovalResponse doctorApprovalResponse = ResponseMakerUtility.getDoctorApprovalResponse(doctorApproval);

        if (AppointmentStatus.CANCELED.name().equals(doctorApprovalResponse.getStatus())) {
            pubSubTemplate.publish("appointment-cancelled-by-doctor", doctorApprovalResponse.getAppointmentId().toString());
        } else {
            this.doctorService.getDoctorById(doctorApproval.getDoctorId())
                    .switchIfEmpty(Mono.error(new InternalServerErrorException("Doctor not found")))
                    .subscribe(doctorResponse -> {
                        String stringifiedDoctorResponse = BasicUtility.stringifyObject(doctorResponse);
                        String stringifiedDoctorApprovalResponse = BasicUtility.stringifyObject(doctorApprovalResponse);
                        pubSubTemplate.publish("appointment-response-by-doctor", stringifiedDoctorApprovalResponse + " <> " + stringifiedDoctorResponse);
                    });
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
                    throw new BadRequestException("Appointment is not in status for doctor approval");
                }
                break;

            case "REJECTED":
                if (AppointmentStatus.RESCHEDULED.name().equals(currentStatus) || AppointmentStatus.SCHEDULED.name().equals(currentStatus) ||
                        AppointmentStatus.APPROVED.name().equals(currentStatus) || AppointmentStatus.CANCELED.name().equals(currentStatus)) {

                    throw new BadRequestException("Rescheduled or Scheduled or Approved or Cancelled appointments cannot be rejected. However You may cancel the appointment");
                }
                doctorApproval.setDoctorComments("Appointment Rejected");
                doctorApproval.setStatus(AppointmentStatus.REJECTED.name());
                break;

            case "CANCELED":
                if (AppointmentStatus.SCHEDULED.name().equals(currentStatus) || AppointmentStatus.RESCHEDULED.name().equals(currentStatus) || AppointmentStatus.APPROVED.name().equals(currentStatus)) {

                    doctorApproval.setDoctorComments("Appointment Canceled");
                    doctorApproval.setStatus(AppointmentStatus.CANCELED.name());

                } else {
                    throw new BadRequestException("Only Scheduled or Rescheduled or Approved appointments can be canceled.");
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

package com.medica.medicamanagement.patient_service.service;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentRescheduleRequest;
import com.medica.dto.NotificationResponse;
import com.medica.exception.BadRequestException;
import com.medica.medicamanagement.patient_service.dao.PatientRepo;
import com.medica.medicamanagement.patient_service.models.Patient;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.UUID;

/**
 * The type Appointment progress service.
 */
@Service
@AllArgsConstructor
@Slf4j
public class AppointmentProgressServiceImpl implements AppointmentProgressService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PatientRepo patientRepo;

    @Override
    public NotificationResponse requestForAppointment(AppointmentRequest request) {
        if (!AppointmentStatus.INITIATED.name().equals(request.getStatus())) {
            throw new BadRequestException("Appointment status must be INITIATED");
        }

        UUID patientId = request.getPatientId();
        Patient patient = this.patientRepo.findById(patientId).orElse(null);
        if (Objects.isNull(patient)) {
            throw new BadRequestException("No Patient Found With Given Id: " + patientId);
        }

        kafkaTemplate.send("appointment_request_by_patient", BasicUtility.stringifyObject(request));
        return BasicUtility.generateNotificationResponse("Appointment Request Successfully Sent", HttpStatus.OK.name());
    }

    @Override
    public NotificationResponse cancelAppointment(String appointmentId) {
        kafkaTemplate.send("appointment-cancelled-by-patient", appointmentId);
        return BasicUtility.generateNotificationResponse("Appointment Cancelled Successfully By Patient", HttpStatus.OK.name());
    }

    @Override
    public NotificationResponse rescheduleAppointment(String appointmentId, AppointmentRescheduleRequest appointmentRescheduleRequest) {
        kafkaTemplate.send("appointment-rescheduled-by-patient", appointmentId + " <> " + BasicUtility.stringifyObject(appointmentRescheduleRequest));
        return BasicUtility.generateNotificationResponse("Appointment Rescheduled Successfully By Patient", HttpStatus.OK.name());
    }
}

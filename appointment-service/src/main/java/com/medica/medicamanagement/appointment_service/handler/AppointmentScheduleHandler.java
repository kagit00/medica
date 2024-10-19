package com.medica.medicamanagement.appointment_service.handler;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.dto.AppointmentRequest;
import com.medica.dto.DoctorResponse;
import com.medica.dto.PatientResponse;
import com.medica.exception.BadRequestException;
import com.medica.medicamanagement.appointment_service.client.DoctorServiceClient;
import com.medica.medicamanagement.appointment_service.client.PatientServiceClient;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.medicamanagement.appointment_service.util.ResponseMakerUtility;
import com.medica.medicamanagement.appointment_service.util.ValidationUtility;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * The type Appointment schedule handler.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentScheduleHandler {
    private final AppointmentRepository appointmentRepository;
    private final PubSubTemplate pubSubTemplate;
    private final DoctorServiceClient doctorService;
    private final PatientServiceClient patientService;

    /**
     * Process appointment and notify.
     *
     * @param request the request
     */
    public void processAppointmentAndNotify(AppointmentRequest request) {
        DoctorResponse doctorResponse = doctorService.getDoctorById(request.getDoctorId());
        PatientResponse patientResponse = patientService.getPatientById(request.getPatientId());

        if (!ValidationUtility.isValid(request, doctorResponse, patientResponse, appointmentRepository)) {
            throw new BadRequestException("Appointment Request Not Valid");
        }

        Appointment appointment = Appointment.builder()
                .patientId(UUID.fromString(request.getPatientId())).doctorId(UUID.fromString(request.getDoctorId()))
                .appointmentDate(request.getAppointmentDate()).status(AppointmentStatus.REVIEWED.name())
                .startTime(request.getTimeRange().getStartTime()).endTime(request.getTimeRange().getEndTime())
                .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                .appointmentDescription("Appointment Request Has Been Reviewed. Sent To Doctor For Approval")
                .build();

        this.appointmentRepository.save(appointment);
        log.info("Appointment Request is valid. Forwarding it to doctor for approval");

        pubSubTemplate.publish(
                "appointment-response-by-appointment-setters",
                BasicUtility.stringifyObject(ResponseMakerUtility.getAppointmentResponse(appointment))
        );
    }
}

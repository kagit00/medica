package com.medica.medicamanagement.appointment_service.handler;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.dto.*;
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
import java.util.Objects;
import java.util.UUID;

/**
 * The type Appointment reschedule handler.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentRescheduleHandler {
    private final AppointmentRepository appointmentRepository;
    private final DoctorServiceClient doctorService;
    private final PatientServiceClient patientService;
    private final PubSubTemplate pubSubTemplate;

    /**
     * Reschedule appointment.
     *
     * @param appointmentId                the appointment id
     * @param appointmentRescheduleRequest the appointment reschedule request
     */
    public void rescheduleAppointment(String appointmentId, AppointmentRescheduleRequest appointmentRescheduleRequest) {
        Appointment appointment = this.appointmentRepository.findById(UUID.fromString(appointmentId)).orElse(null);

        if (!Objects.isNull(appointment) && AppointmentStatus.SCHEDULED.name().equals(appointment.getStatus())) {

            AppointmentRequest appointmentRequest = AppointmentRequest.builder().status(appointment.getStatus())
                    .appointmentDate(appointmentRescheduleRequest.getAppointmentDate()).doctorId(String.valueOf(appointment.getDoctorId()))
                    .timeRange(appointmentRescheduleRequest.getTimeRange()).patientId(String.valueOf(appointment.getPatientId()))
                    .build();

            DoctorResponse doctorResponse = this.doctorService.getDoctorById(appointment.getDoctorId().toString());
            PatientResponse patientResponse = this.patientService.getPatientById(appointment.getPatientId().toString());

            if (!ValidationUtility.isValid(appointmentRequest, doctorResponse, patientResponse, appointmentRepository)) {
                throw new BadRequestException("Appointment Request not valid");
            }

            appointment.setAppointmentDescription("Appointment Reschedule Request Initiated");
            appointment.setAppointmentDate(appointmentRescheduleRequest.getAppointmentDate());
            appointment.setStartTime(appointmentRescheduleRequest.getTimeRange().getStartTime());
            appointment.setEndTime(appointmentRescheduleRequest.getTimeRange().getEndTime());
            appointment.setStatus(AppointmentStatus.RESCHEDULE_REQUESTED.name());
            appointment.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

            log.info("Appointment Reschedule Request Initiated Successfully");
            this.appointmentRepository.save(appointment);

            AppointmentResponse appointmentResponse = ResponseMakerUtility.getAppointmentResponse(appointment);
            pubSubTemplate.publish(
                    "appointment-rescheduled-by-appointment-setters-at-patient-request",
                    BasicUtility.stringifyObject(appointmentResponse)
            );

        } else {
            throw new BadRequestException("Appointment Id should be valid & appointment must be in scheduled state");
        }
    }
}

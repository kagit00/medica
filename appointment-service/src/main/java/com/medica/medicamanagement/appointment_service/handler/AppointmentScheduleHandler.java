package com.medica.medicamanagement.appointment_service.handler;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.DoctorResponse;
import com.medica.dto.PatientResponse;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * The type Appointment schedule handler.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentScheduleHandler {
    private final AppointmentRepository appointmentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DoctorServiceClient doctorService;
    private final PatientServiceClient patientService;

    /**
     * Process appointment and notify.
     *
     * @param request the request
     */
    public void processAppointmentAndNotify(AppointmentRequest request) {
        DoctorResponse doctorResponse = doctorService.getDoctorById(request.getDoctorId().toString());
        PatientResponse patientResponse = patientService.getPatientById(request.getPatientId().toString());

        if (!ValidationUtility.isValid(request, doctorResponse, patientResponse, appointmentRepository)) {
            log.error("Appointment Request Not Valid");
            return;
        }

        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId()).doctorId(request.getDoctorId()).appointmentDate(request.getAppointmentDate())
                .status(AppointmentStatus.REVIEWED.name()).startTime(request.getTimeRange().getStartTime()).endTime(request.getTimeRange().getEndTime())
                .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                .build();

        this.appointmentRepository.save(appointment);

        log.info("Appointment Request is valid. Forwarding it to doctor for approval");
        kafkaTemplate.send("appointment_response_by_appointment_setters", BasicUtility.stringifyObject(ResponseMakerUtility.getAppointmentResponse(appointment)));
    }
}

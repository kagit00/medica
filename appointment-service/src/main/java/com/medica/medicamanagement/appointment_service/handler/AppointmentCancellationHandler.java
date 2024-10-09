package com.medica.medicamanagement.appointment_service.handler;

import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorResponse;
import com.medica.dto.PatientResponse;
import com.medica.medicamanagement.appointment_service.client.DoctorServiceClient;
import com.medica.medicamanagement.appointment_service.client.PatientServiceClient;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.medicamanagement.appointment_service.util.ResponseMakerUtility;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentCancellationHandler {
    private final AppointmentRepository appointmentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DoctorServiceClient doctorService;
    private final PatientServiceClient patientService;

    public void cancelAppointment(String appointmentId, boolean isCanceledByPatient) {
        Appointment appointment = appointmentRepository.findById(UUID.fromString(appointmentId)).orElse(null);

        if (!Objects.isNull(appointment) && AppointmentStatus.SCHEDULED.name().equals(appointment.getStatus())) {
            appointment.setStatus(AppointmentStatus.CANCELED.name());
            appointment.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
            appointment.setAppointmentDescription(isCanceledByPatient? "Appointment Cancelled by Patient" : "Appointment Cancelled By Doctor");
            appointment.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

            this.appointmentRepository.save(appointment);

            DoctorResponse doctorResponse = this.doctorService.getDoctorById(appointment.getDoctorId().toString());
            PatientResponse patientResponse = this.patientService.getPatientById(appointment.getPatientId().toString());

            notifyPatientAndDoctor(doctorResponse, patientResponse, appointment, isCanceledByPatient);

            if (!isCanceledByPatient) {
                kafkaTemplate.send("process-refund-to-patient", BasicUtility.stringifyObject(appointment));
            }
            return;
        }
        log.error("Appointment id should not be null or in scheduled state for appointment cancellation");
    }

    private void notifyPatientAndDoctor(DoctorResponse doctorResponse, PatientResponse patientResponse, Appointment appointment, boolean isCanceledByPatient) {
        AppointmentResponse appointmentResponse = ResponseMakerUtility.getAppointmentResponse(appointment);
        if (isCanceledByPatient) {
            kafkaTemplate.send("appointment-cancelled-by-patient", appointment.getId().toString());
        }

        kafkaTemplate.send(
                "appointment-status-mail-for-patient",
                BasicUtility.stringifyObject(doctorResponse) + " <> " + BasicUtility.stringifyObject(patientResponse) + " <> " +
                        BasicUtility.stringifyObject(appointmentResponse) + " <> " + " " + " <> " + isCanceledByPatient
        );

        kafkaTemplate.send(
                "appointment-status-mail-for-doctor",
                BasicUtility.stringifyObject(doctorResponse) + " <> " + BasicUtility.stringifyObject(patientResponse) + " <> " +
                        BasicUtility.stringifyObject(appointmentResponse) + " <> " + " " + " <> " + isCanceledByPatient
        );
    }

    public void notifyRefundStatusToPatient(String appointmentId, String status) {
        //TODO: sms or push messages
    }
}

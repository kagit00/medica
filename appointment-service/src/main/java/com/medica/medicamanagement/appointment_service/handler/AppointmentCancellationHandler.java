package com.medica.medicamanagement.appointment_service.handler;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorResponse;
import com.medica.dto.PatientResponse;
import com.medica.dto.PaymentStatus;
import com.medica.exception.BadRequestException;
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
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.UUID;

/**
 * The type Appointment cancellation handler.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentCancellationHandler {
    private final AppointmentRepository appointmentRepository;
    private final PubSubTemplate pubSubTemplate;
    private final DoctorServiceClient doctorService;
    private final PatientServiceClient patientService;

    /**
     * Cancel appointment.
     *
     * @param appointmentId       the appointment id
     * @param isCanceledByPatient the is canceled by patient
     */
    public void cancelAppointment(String appointmentId, boolean isCanceledByPatient) {
        Appointment appointment = appointmentRepository.findById(UUID.fromString(appointmentId)).orElse(null);

        if (!Objects.isNull(appointment) && (AppointmentStatus.SCHEDULED.name().equals(appointment.getStatus()) ||
                AppointmentStatus.RESCHEDULED.name().equals(appointment.getStatus()) || AppointmentStatus.APPROVED.name().equals(appointment.getStatus()))) {

            appointment.setStatus(AppointmentStatus.CANCELED.name());
            appointment.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
            appointment.setAppointmentDescription(isCanceledByPatient? "Appointment Cancelled by Patient" : "Appointment Cancelled By Doctor");
            appointment.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

            this.appointmentRepository.save(appointment);

            DoctorResponse doctorResponse = this.doctorService.getDoctorById(appointment.getDoctorId().toString());
            PatientResponse patientResponse = this.patientService.getPatientById(appointment.getPatientId().toString());

            notifyPatientAndDoctor(doctorResponse, patientResponse, appointment, isCanceledByPatient);

            if (!isCanceledByPatient) {
                pubSubTemplate.publish("process-refund-to-patient", BasicUtility.stringifyObject(appointment));
            }
            return;
        }
        throw new BadRequestException("Appointment id should not be null or in scheduled/approved/rescheduled state for appointment cancellation");
    }

    private void notifyPatientAndDoctor(DoctorResponse doctorResponse, PatientResponse patientResponse, Appointment appointment, boolean isCanceledByPatient) {
        AppointmentResponse appointmentResponse = ResponseMakerUtility.getAppointmentResponse(appointment);
        if (isCanceledByPatient) {
            pubSubTemplate.publish("appointment-cancelled-by-patient", appointment.getId().toString());
        }

        pubSubTemplate.publish("appointment-status-mail-for-patient", BasicUtility.stringifyObject(doctorResponse) + " <> "
                + BasicUtility.stringifyObject(patientResponse) + " <> " + BasicUtility.stringifyObject(appointmentResponse) + " <> " + " "
                + " <> " + isCanceledByPatient
        );

        pubSubTemplate.publish("appointment-status-mail-for-doctor", BasicUtility.stringifyObject(doctorResponse) + " <> "
                + BasicUtility.stringifyObject(patientResponse) + " <> " + BasicUtility.stringifyObject(appointmentResponse) + " <> " + " "
                + " <> " + isCanceledByPatient
        );
    }

    /**
     * Notify refund status to patient.
     *
     * @param appointmentId the appointment id
     * @param status        the status
     */
    public void notifyRefundStatusToPatient(String appointmentId, String status) {
        if (PaymentStatus.REFUNDED.name().equals(status)) {
            // pubSubTemplate.publish("appointment-status-mail-for-patient", "");
        }
    }
}

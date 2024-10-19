package com.medica.medicamanagement.appointment_service.handler;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorResponse;
import com.medica.dto.PatientResponse;
import com.medica.dto.PaymentStatus;
import com.medica.medicamanagement.appointment_service.client.DoctorServiceClient;
import com.medica.medicamanagement.appointment_service.client.PatientServiceClient;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.medicamanagement.appointment_service.util.ResponseMakerUtility;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The type Payment status handler.
 */
@Service
@RequiredArgsConstructor
public class PaymentStatusHandler {
    private final DoctorServiceClient doctorService;
    private final PatientServiceClient patientService;
    private final AppointmentRepository appointmentRepository;
    private final PubSubTemplate pubSubTemplate;

    /**
     * Notify patient and doctor.
     *
     * @param appointment     the appointment
     * @param doctorResponse  the doctor response
     * @param patientResponse the patient response
     */
    public void notifyPatientAndDoctor(Appointment appointment, DoctorResponse doctorResponse, PatientResponse patientResponse) {

        AppointmentResponse appointmentResponse = ResponseMakerUtility.getAppointmentResponse(appointment);

        pubSubTemplate.publish(
                "appointment-response-by-appointment-setters",
                BasicUtility.stringifyObject(appointmentResponse)
        );

        pubSubTemplate.publish(
                "appointment-status-mail-for-patient",
                BasicUtility.stringifyObject(doctorResponse) + " <> "
                        + BasicUtility.stringifyObject(patientResponse) + " <> "
                        + BasicUtility.stringifyObject(appointmentResponse)
        );

        pubSubTemplate.publish(
                "appointment-status-mail-for-doctor",
                BasicUtility.stringifyObject(doctorResponse) + " <> "
                        + BasicUtility.stringifyObject(patientResponse) + " <> "
                        + BasicUtility.stringifyObject(appointmentResponse)
        );
    }

    /**
     * Update appointment status.
     *
     * @param appointment the appointment
     * @param status      the status
     */
    public void updateAppointmentStatus(Appointment appointment, String status) {
        DoctorResponse doctorResponse = this.doctorService.getDoctorById(appointment.getDoctorId().toString());
        PatientResponse patientResponse = this.patientService.getPatientById(appointment.getPatientId().toString());

        if (PaymentStatus.SUCCESS.name().equals(status)) {
            appointment.setStatus(AppointmentStatus.SCHEDULED.name());
        }

        this.appointmentRepository.save(appointment);
        notifyPatientAndDoctor(appointment, doctorResponse, patientResponse);
    }
}

package com.medica.medicamanagement.appointment_service.handler;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorApprovalResponse;
import com.medica.dto.DoctorResponse;
import com.medica.dto.PatientResponse;
import com.medica.medicamanagement.appointment_service.client.PatientServiceClient;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.medicamanagement.appointment_service.util.ResponseMakerUtility;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * The type Doctor approval handler.
 */
@Service
@RequiredArgsConstructor
public class DoctorApprovalHandler {
    private final PubSubTemplate pubSubTemplate;
    private final Environment env;
    private final AppointmentRepository appointmentRepository;
    private final PatientServiceClient patientService;


    /**
     * Update appointment status and notify.
     *
     * @param doctorResponse   the doctor response
     * @param appointment      the appointment
     * @param approvalResponse the approval response
     */
    public void updateAppointmentStatusAndNotify(DoctorResponse doctorResponse, Appointment appointment, DoctorApprovalResponse approvalResponse) {
        PatientResponse patientResponse = this.patientService.getPatientById(appointment.getPatientId().toString());

        if (AppointmentStatus.RESCHEDULE_REQUESTED.name().equals(appointment.getStatus()) && AppointmentStatus.RESCHEDULED.name().equals(approvalResponse.getStatus())) {
            appointment.setStatus(AppointmentStatus.RESCHEDULED.name());
            appointment.setAppointmentDescription("Appointment Rescheduled");
        } else {
            appointment.setStatus(approvalResponse.getStatus());
            appointment.setAppointmentDescription(appointment.getStatus());
        }

        appointment.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
        this.appointmentRepository.save(appointment);

        notifyPatientAndDoctor(doctorResponse, patientResponse, ResponseMakerUtility.getAppointmentResponse(appointment));
    }

    private void notifyPatientAndDoctor(DoctorResponse doctorResponse, PatientResponse patientResponse, AppointmentResponse appointmentResponse) {

        String paymentServiceUrl = AppointmentStatus.APPROVED.name().equals(appointmentResponse.getStatus())?
                UriComponentsBuilder.fromHttpUrl(env.getProperty("payment.server.domain") + "/payment/payment-interface")
                .queryParam("appointmentId", appointmentResponse.getId()).queryParam("amount", doctorResponse.getFee()).toUriString() : "";

        pubSubTemplate.publish(
                "appointment-status-mail-for-patient",
                BasicUtility.stringifyObject(doctorResponse) + " <> "
                        + BasicUtility.stringifyObject(patientResponse) + " <> "
                        + BasicUtility.stringifyObject(appointmentResponse) + " <> "
                        + paymentServiceUrl
        );

        pubSubTemplate.publish(
                "appointment-status-mail-for-doctor",
                BasicUtility.stringifyObject(doctorResponse) + " <> "
                        + BasicUtility.stringifyObject(patientResponse) + " <> "
                        + BasicUtility.stringifyObject(appointmentResponse)
        );
    }
}

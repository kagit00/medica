package com.medica.medicamanagement.appointment_service.handler;

import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorApprovalResponse;
import com.medica.dto.DoctorResponse;
import com.medica.dto.PatientResponse;
import com.medica.medicamanagement.appointment_service.client.PatientServiceClient;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.medicamanagement.appointment_service.util.ResponseMakerUtility;
import com.medica.util.BasicUtility;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorApprovalHandler {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Environment env;
    private final AppointmentRepository appointmentRepository;
    private final PatientServiceClient patientService;

    public void updateAppointmentStatusAndNotify(DoctorResponse doctorResponse, Appointment appointment, DoctorApprovalResponse approvalResponse) {
        PatientResponse patientResponse = this.patientService.getPatientById(appointment.getPatientId().toString());

        appointment.setStatus(approvalResponse.getStatus());
        appointment.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
        this.appointmentRepository.save(appointment);

        notifyPatientAndDoctor(doctorResponse, patientResponse, ResponseMakerUtility.getAppointmentResponse(appointment));
    }

    private void notifyPatientAndDoctor(DoctorResponse doctorResponse, PatientResponse patientResponse, AppointmentResponse appointmentResponse) {
        kafkaTemplate.send(
                "appointment-status-mail-for-patient",
                BasicUtility.stringifyObject(doctorResponse) + " <> " + BasicUtility.stringifyObject(patientResponse) + " <> " +
                        BasicUtility.stringifyObject(appointmentResponse) + " <> " + env.getProperty("payment.server.domain") + "/payment-interface?appointmentId="
                        + appointmentResponse.getId() + "&amount=" + doctorResponse.getFee()
        );

        kafkaTemplate.send(
                "appointment-status-mail-for-doctor",
                BasicUtility.stringifyObject(doctorResponse) + " <> " + BasicUtility.stringifyObject(patientResponse) + " <> " +
                        BasicUtility.stringifyObject(appointmentResponse)
        );
    }
}

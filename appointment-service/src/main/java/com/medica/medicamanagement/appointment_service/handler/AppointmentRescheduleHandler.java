package com.medica.medicamanagement.appointment_service.handler;

import com.medica.dto.AppointmentRescheduleRequest;
import com.medica.dto.AppointmentResponse;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentRescheduleHandler {
    private final AppointmentRepository appointmentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void rescheduleAppointment(String appointmentId, AppointmentRescheduleRequest appointmentRescheduleRequest) {
        Appointment appointment = this.appointmentRepository.findById(UUID.fromString(appointmentId)).orElse(null);

        if (!Objects.isNull(appointment) && AppointmentStatus.SCHEDULED.name().equals(appointment.getStatus())) {
            appointment.setAppointmentDescription("Appointment Reschedule Request Initiated");
            appointment.setAppointmentDate(appointmentRescheduleRequest.getAppointmentDate());
            appointment.setStartTime(appointmentRescheduleRequest.getTimeRange().getStartTime());
            appointment.setEndTime(appointmentRescheduleRequest.getTimeRange().getEndTime());
            appointment.setStatus(AppointmentStatus.RESCHEDULE_REQUESTED.name());
            appointment.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

            log.info("Appointment Rescheduled Successfully");
            this.appointmentRepository.save(appointment);

            AppointmentResponse appointmentResponse = ResponseMakerUtility.getAppointmentResponse(appointment);
            kafkaTemplate.send("appointment-rescheduled-by-patient", BasicUtility.stringifyObject(appointmentResponse));

        } else {
            log.error("Appointment Id should be valid & appointment must be in scheduled state");
        }
    }
}

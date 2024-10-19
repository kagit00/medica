package com.medica.medicamanagement.appointment_service.listener;

import com.medica.medicamanagement.appointment_service.service.AppointmentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentCancellationListener {
    private final AppointmentProcessingService appointmentProcessingService;

    /**
     * Handle appointment cancellation on patient request.
     *
     * @param appointmentId the appointment id
     */
    public void handleAppointmentCancellationOnPatientReq(String appointmentId) {
        appointmentProcessingService.cancelAppointment(appointmentId, true);
    }

    /**
     * Handle appointment cancellation on doctor request.
     *
     * @param appointmentId the appointment id
     */
    public void handleAppointmentCancellationOnDoctorReq(String appointmentId) {
        appointmentProcessingService.cancelAppointment(appointmentId, false);
    }
}

package com.medica.medicamanagement.notification_service.listener;

import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.notification_service.service.AppointmentStatusEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentStatusNotificationListener {

    private final AppointmentStatusEmailService appointmentStatusEmailService;

    /**
     * Receive appointment status as patient.
     *
     * @param response the response
     */
    public void receiveAppointmentStatusAsPatient(String response) {
        try {
            appointmentStatusEmailService.sendEmailToPatient(response);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    /**
     * Receive appointment status as doctor.
     *
     * @param response the response
     */
    public void receiveAppointmentStatusAsDoctor(String response) {
        try {
            appointmentStatusEmailService.sendEmailToDoctor(response);
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}

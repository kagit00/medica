package com.medica.medicamanagement.doctor_service.listener;

import com.medica.medicamanagement.doctor_service.handler.AppointmentRescheduleHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentRescheduleListener {
    private final AppointmentRescheduleHandler appointmentRescheduleHandler;

    public void handleAppointmentRescheduleAtPatientReq(String response) {
        appointmentRescheduleHandler.handleRescheduleAppointmentAtPatientReq(response);
    }
}

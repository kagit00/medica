package com.medica.medicamanagement.doctor_service.listener;

import com.medica.medicamanagement.doctor_service.service.AppointmentProgressService;
import com.medica.model.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentRetryListener {
    private final AppointmentProgressService appointmentProgressService;

    public void handleAppointmentRetry(String appointmentId) {
        appointmentProgressService.updateAppointmentStatus(UUID.fromString(appointmentId), AppointmentStatus.PENDING.name());
    }
}

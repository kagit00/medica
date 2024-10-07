package com.medica.medicamanagement.patient_service.service;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.NotificationResponse;

public interface AppointmentStatusUpdateRequestService {
    NotificationResponse requestForAppointment(AppointmentRequest request);
    NotificationResponse cancelAppointment(String appointmentId);
}

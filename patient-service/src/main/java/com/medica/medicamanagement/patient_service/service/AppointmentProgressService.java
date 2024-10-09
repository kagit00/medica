package com.medica.medicamanagement.patient_service.service;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentRescheduleRequest;
import com.medica.dto.NotificationResponse;

public interface AppointmentProgressService {
    NotificationResponse requestForAppointment(AppointmentRequest request);
    NotificationResponse cancelAppointment(String appointmentId);
    NotificationResponse rescheduleAppointment(String appointmentId, AppointmentRescheduleRequest appointmentRescheduleRequest);
}

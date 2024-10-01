package com.medica.medicamanagement.appointment_service.service;

import com.medica.medicamanagement.appointment_service.model.AppointmentHistory;
import java.util.List;
import java.util.UUID;

public interface AppointmentHistoryService {
    List<AppointmentHistory> getHistoryByAppointmentId(UUID appointmentId);
    AppointmentHistory createHistory(UUID appointmentId, AppointmentHistory history);
}
package com.medica.medicamanagement.appointment_service.service;



import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentResponse;

import java.util.List;
import java.util.UUID;

public interface AppointmentService {
    AppointmentResponse createAppointment(AppointmentRequest request);
    List<AppointmentResponse> getAllAppointments();
    AppointmentResponse getAppointmentById(UUID id);
    AppointmentResponse updateAppointment(UUID id, AppointmentRequest request);
    void deleteAppointment(UUID id);
}
package com.medica.medicamanagement.doctor_service.service;

import com.medica.dto.DoctorApprovalResponse;

import java.util.UUID;

public interface AppointmentStatusUpdateService {
    DoctorApprovalResponse updateAppointmentStatus(UUID appointmentId, String status);
}

package com.medica.medicamanagement.doctor_service.service;

import com.medica.dto.DoctorApprovalResponse;

import java.util.UUID;

public interface AppointmentProgressService {
    DoctorApprovalResponse updateAppointmentStatus(UUID appointmentId, String status);
}

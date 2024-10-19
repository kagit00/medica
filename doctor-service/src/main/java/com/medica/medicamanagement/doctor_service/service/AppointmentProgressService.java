package com.medica.medicamanagement.doctor_service.service;

import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorApprovalResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * The interface Appointment progress service.
 */
public interface AppointmentProgressService {
    /**
     * Update appointment status doctor approval response.
     *
     * @param appointmentId the appointment id
     * @param status        the status
     * @return the doctor approval response
     */
    DoctorApprovalResponse updateAppointmentStatus(UUID appointmentId, String status);
}

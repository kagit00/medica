package com.medica.medicamanagement.appointment_service.service;



import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentResponse;

import java.util.List;
import java.util.UUID;

/**
 * The interface Appointment service.
 */
public interface AppointmentService {
    /**
     * Create appointment appointment response.
     *
     * @param request the request
     * @return the appointment response
     */
    AppointmentResponse createAppointment(AppointmentRequest request);

    /**
     * Gets all appointments.
     *
     * @return the all appointments
     */
    List<AppointmentResponse> getAllAppointments();

    /**
     * Gets appointment by id.
     *
     * @param id the id
     * @return the appointment by id
     */
    AppointmentResponse getAppointmentById(UUID id);

    /**
     * Update appointment appointment response.
     *
     * @param id      the id
     * @param request the request
     * @return the appointment response
     */
    AppointmentResponse updateAppointment(UUID id, AppointmentRequest request);

    /**
     * Delete appointment.
     *
     * @param id the id
     */
    void deleteAppointment(UUID id);
}
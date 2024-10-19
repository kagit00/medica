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

    /**
     * Gets appointment info.
     *
     * @param patientId the patient id
     * @param date      the date
     * @param startTime the start time
     * @param endTime   the end time
     * @return the appointment info
     */
    AppointmentResponse getAppointmentInfo(UUID patientId, String date, String startTime, String endTime);

    /**
     * Gets all appointments.
     *
     * @param patientId the patient id
     * @return the all appointments
     */
    List<AppointmentResponse> getAllAppointments(UUID patientId);

    /**
     * Gets appointment info for doctor.
     *
     * @param doctorId  the doctor id
     * @param date      the date
     * @param startTime the start time
     * @param endTime   the end time
     * @return the appointment info for doctor
     */
    AppointmentResponse getAppointmentInfoForDoctor(UUID doctorId, String date, String startTime, String endTime);

    /**
     * Gets all appointments for doctor.
     *
     * @param doctorId the doctor id
     * @return the all appointments for doctor
     */
    List<AppointmentResponse> getAllAppointmentsForDoctor(UUID doctorId);

}
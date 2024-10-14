package com.medica.medicamanagement.patient_service.service;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentRescheduleRequest;
import com.medica.dto.NotificationResponse;

/**
 * The interface Appointment progress service.
 */
public interface AppointmentProgressService {
    /**
     * Request for appointment notification response.
     *
     * @param request the request
     * @return the notification response
     */
    NotificationResponse requestForAppointment(AppointmentRequest request);

    /**
     * Cancel appointment notification response.
     *
     * @param appointmentId the appointment id
     * @return the notification response
     */
    NotificationResponse cancelAppointment(String appointmentId);

    /**
     * Reschedule appointment notification response.
     *
     * @param appointmentId                the appointment id
     * @param appointmentRescheduleRequest the appointment reschedule request
     * @return the notification response
     */
    NotificationResponse rescheduleAppointment(String appointmentId, AppointmentRescheduleRequest appointmentRescheduleRequest);
}

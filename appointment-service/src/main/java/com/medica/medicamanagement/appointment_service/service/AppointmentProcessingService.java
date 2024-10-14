package com.medica.medicamanagement.appointment_service.service;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentRescheduleRequest;
import com.medica.dto.DoctorApprovalResponse;
import com.medica.dto.DoctorResponse;

/**
 * The interface Appointment processing service.
 */
public interface AppointmentProcessingService {
    /**
     * Handle appointment schedule request.
     *
     * @param request the request
     */
    void handleAppointmentScheduleRequest(AppointmentRequest request);

    /**
     * Handle doctor response.
     *
     * @param response       the response
     * @param doctorResponse the doctor response
     */
    void handleDoctorResponse(DoctorApprovalResponse response, DoctorResponse doctorResponse);

    /**
     * Handle payment status.
     *
     * @param appointmentId the appointment id
     * @param status        the status
     */
    void handlePaymentStatus(String appointmentId, String status);

    /**
     * Cancel appointment.
     *
     * @param appointmentId       the appointment id
     * @param isCanceledByPatient the is canceled by patient
     */
    void cancelAppointment(String appointmentId, boolean isCanceledByPatient);

    /**
     * Handle refund status.
     *
     * @param appointmentId the appointment id
     * @param status        the status
     */
    void handleRefundStatus(String appointmentId, String status);

    /**
     * Reschedule appointment.
     *
     * @param appointmentId                the appointment id
     * @param appointmentRescheduleRequest the appointment reschedule request
     */
    void rescheduleAppointment(String appointmentId, AppointmentRescheduleRequest appointmentRescheduleRequest);
}

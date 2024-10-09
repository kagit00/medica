package com.medica.medicamanagement.appointment_service.service;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentRescheduleRequest;
import com.medica.dto.DoctorApprovalResponse;
import com.medica.dto.DoctorResponse;

public interface AppointmentProcessingService {
    void handleAppointmentScheduleRequest(AppointmentRequest request);
    void handleDoctorResponse(DoctorApprovalResponse response, DoctorResponse doctorResponse);
    void handlePaymentStatus(String appointmentId, String status);
    void cancelAppointment(String appointmentId, boolean isCanceledByPatient);
    void handleRefundStatus(String appointmentId, String status);
    void rescheduleAppointment(String appointmentId, AppointmentRescheduleRequest appointmentRescheduleRequest);
}

package com.medica.medicamanagement.appointment_service.util;

import com.medica.dto.AppointmentResponse;
import com.medica.medicamanagement.appointment_service.model.Appointment;

public final class ResponseMakerUtility {
    private ResponseMakerUtility() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    public static AppointmentResponse getAppointmentResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId()).doctorId(appointment.getDoctorId())
                .appointmentDate(appointment.getAppointmentDate()).status(appointment.getStatus())
                .startTime(appointment.getStartTime()).endTime(appointment.getEndTime())
                .build();
    }
}

package com.medica.medicamanagement.appointment_service.util;

import com.medica.dto.DoctorResponse;
import com.medica.medicamanagement.appointment_service.model.Appointment;


public final class EmailContentUtility {
    private EmailContentUtility() {
        throw new UnsupportedOperationException("Unsupported operation");
    }

    public static String getEmailContent(DoctorResponse doctorResponse, Appointment request) {
        return "{" +
                "\"doctorName\": \"" + doctorResponse.getName() + "\"," +
                "\"specialization\": \"" + doctorResponse.getSpecialization().getName() + "\"," +
                "\"appointmentDate\": \"" + request.getAppointmentDate() + "\"," +
                "\"startTime\": \"" + request.getStartTime() + "\"," +
                "\"endTime\": \"" + request.getEndTime() + "\"," +
                "\"appointmentStatus\": \"" + request.getStatus() + "\"," +
                "\"appointmentFee\": \"" + doctorResponse.getFee() + "\"," +
                "\"paymentLink\": \"" + "http://localhost:8083/payment-interface?appointmentId="
                + request.getId()
                + "&amount="
                + doctorResponse.getFee() + "\"" +
                "}";
    }
}

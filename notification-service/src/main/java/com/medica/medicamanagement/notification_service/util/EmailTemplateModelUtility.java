package com.medica.medicamanagement.notification_service.util;

import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorResponse;
import com.medica.dto.PatientResponse;
import com.medica.util.BasicUtility;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Email template model utility.
 */
public final class EmailTemplateModelUtility {

    private EmailTemplateModelUtility() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Create template model for appointment status map.
     *
     * @param doctorResponse      the doctor response
     * @param patientResponse     the patient response
     * @param appointmentResponse the appointment response
     * @param paymentLink         the payment link
     * @param isPatient           the is patient
     * @return the map
     */
    public static Map<String, Object> createTemplateModelForAppointmentStatus(DoctorResponse doctorResponse, PatientResponse patientResponse, AppointmentResponse appointmentResponse, String paymentLink, boolean isPatient) {
        Map<String, Object> templateModel = new HashMap<>();
        if (isPatient) {
            templateModel.put("name", patientResponse.getFirstName());
            templateModel.put("doctorName", doctorResponse.getFirstName() + " " + doctorResponse.getLastName());
            templateModel.put("specialization", doctorResponse.getSpecialization().getName());
            templateModel.put("paymentLink", paymentLink);
        } else {
            templateModel.put("name", doctorResponse.getFirstName() + " " + doctorResponse.getLastName());
            templateModel.put("patientName", patientResponse.getFirstName() + " " + patientResponse.getLastName());
        }

        templateModel.put("appointmentDate", BasicUtility.toDate(appointmentResponse.getAppointmentDate()));
        templateModel.put("startTime", appointmentResponse.getStartTime());
        templateModel.put("endTime", appointmentResponse.getEndTime());
        templateModel.put("appointmentFee", doctorResponse.getFee());

        return templateModel;
    }

    public static Map<String, Object> createTemplateModelForPasswordChange(String name) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("name", name);
        return templateModel;
    }
}

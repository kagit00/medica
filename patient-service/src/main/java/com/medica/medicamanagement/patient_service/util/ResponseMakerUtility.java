package com.medica.medicamanagement.patient_service.util;

import com.medica.dto.PatientResponse;
import com.medica.medicamanagement.patient_service.models.Patient;

public final class ResponseMakerUtility {

    private ResponseMakerUtility() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    public static PatientResponse getPatientResponse(Patient patient) {
        return PatientResponse.builder().id(patient.getId())
                .firstName(patient.getFirstName()).lastName(patient.getLastName()).phone(patient.getPhone())
                .emailId(patient.getEmailId()).address(patient.getAddress()).dob(patient.getDob())
                .medicalHistory(patient.getMedicalHistory())
                .build();
    }
}

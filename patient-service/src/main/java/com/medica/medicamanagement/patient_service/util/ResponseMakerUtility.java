package com.medica.medicamanagement.patient_service.util;

import com.medica.dto.PatientResponse;
import com.medica.dto.UserResponse;
import com.medica.medicamanagement.patient_service.models.Patient;

/**
 * The type Response maker utility.
 */
public final class ResponseMakerUtility {

    private ResponseMakerUtility() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    /**
     * Gets patient response.
     *
     * @param patient      the patient
     * @param userResponse the user response
     * @return the patient response
     */
    public static PatientResponse getPatientResponse(Patient patient, UserResponse userResponse) {
        return PatientResponse.builder().id(patient.getId())
                .firstName(userResponse.getFirstName()).lastName(userResponse.getLastName()).phone(userResponse.getPhone())
                .email(userResponse.getEmail()).address(userResponse.getAddress()).age(userResponse.getAge())
                .medicalHistory(patient.getMedicalHistory()).username(userResponse.getUsername())
                .build();
    }
}

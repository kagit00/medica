package com.medica.medicamanagement.appointment_service.client;

import com.medica.dto.PatientResponse;

/**
 * The interface Patient service client.
 */
public interface PatientServiceClient {
    /**
     * Gets patient by id.
     *
     * @param id the id
     * @return the patient by id
     */
    PatientResponse getPatientById(String id);
}

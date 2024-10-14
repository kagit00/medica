package com.medica.medicamanagement.patient_service.service;

import com.medica.dto.PatientRequest;
import com.medica.dto.PatientResponse;
import com.medica.medicamanagement.patient_service.models.Patient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * The interface Patient service.
 */
public interface PatientService {
    /**
     * Create patient mono.
     *
     * @param patientRequest the patient request
     * @return the mono
     */
    Mono<PatientResponse> createPatient(PatientRequest patientRequest);

    /**
     * Delete patient mono.
     *
     * @param patientId the patient id
     * @return the mono
     */
    Mono<Void> deletePatient(UUID patientId);

    /**
     * Update patient mono.
     *
     * @param patientRequest the patient request
     * @param patientId      the patient id
     * @return the mono
     */
    Mono<PatientResponse> updatePatient(PatientRequest patientRequest, UUID patientId);

    /**
     * Gets patient by id.
     *
     * @param patientId the patient id
     * @return the patient by id
     */
    Mono<PatientResponse> getPatientById(UUID patientId);

    /**
     * Gets all patients.
     *
     * @return the all patients
     */
    Mono<List<PatientResponse>> getAllPatients();
}

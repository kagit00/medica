package com.medica.medicamanagement.patient_service.util;

import com.medica.exception.BadRequestException;
import com.medica.medicamanagement.patient_service.dao.PatientRepo;
import com.medica.medicamanagement.patient_service.models.Patient;
import com.medica.util.Constant;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * The type Repository utility.
 */
public final class RepositoryUtility {

    private RepositoryUtility() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Gets patient by id.
     *
     * @param patientId   the patient id
     * @param patientRepo the patient repo
     * @return the patient by id
     */
    public static Mono<Patient> getPatientById(UUID patientId, PatientRepo patientRepo) {
        return Mono.fromCallable(() -> patientRepo.findById(patientId)
                .orElseThrow(() -> new BadRequestException("Patient not found")));
    }

}

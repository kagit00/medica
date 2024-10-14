package com.medica.medicamanagement.appointment_service.client;

import com.medica.dto.DoctorResponse;

/**
 * The interface Doctor service client.
 */
public interface DoctorServiceClient {
    /**
     * Gets doctor by id.
     *
     * @param id the id
     * @return the doctor by id
     */
    DoctorResponse getDoctorById(String id);
}

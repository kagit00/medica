package com.medica.medicamanagement.appointment_service.client;

import com.medica.dto.PatientResponse;

public interface PatientServiceClient {
    PatientResponse getPatientById(String id);
}

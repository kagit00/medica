package com.medica.medicamanagement.appointment_service.client;

import com.medica.dto.DoctorResponse;

public interface DoctorServiceClient {
    DoctorResponse getDoctorById(String id);
}

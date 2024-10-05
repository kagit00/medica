package com.medica.medicamanagement.patient_service.service;

import com.medica.medicamanagement.patient_service.models.Patient;

import java.util.Map;

public interface EmailService {
    void sendEmail(Patient patient, String response);
}

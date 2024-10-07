package com.medica.medicamanagement.patient_service.service;

import com.medica.dto.PatientRequest;
import com.medica.dto.PatientResponse;
import com.medica.medicamanagement.patient_service.models.Patient;

import java.util.List;
import java.util.UUID;

public interface PatientService {
    PatientResponse createPatient(PatientRequest patientRequest);
    void deletePatient(UUID patientId);
    PatientResponse updatePatient(PatientRequest patientRequest, UUID patientId);
    Patient getPatientById(UUID patientId);
    List<Patient> getAllPatients();
}

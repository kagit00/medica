package com.medica.medicamanagement.patient_service.dao;

import com.medica.medicamanagement.patient_service.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRepo extends JpaRepository<Patient, UUID> {
}

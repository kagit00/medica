package com.medica.medicamanagement.patient_service.service;

import com.medica.dto.PatientRequest;
import com.medica.dto.PatientResponse;
import com.medica.medicamanagement.patient_service.dao.PatientRepo;
import com.medica.medicamanagement.patient_service.models.Patient;
import com.medica.medicamanagement.patient_service.util.ResponseMakerUtility;
import com.medica.util.Constant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PatientServiceImplementation implements PatientService {
    private final PatientRepo patientRepo;

    @Override
    public PatientResponse createPatient(PatientRequest patientRequest) {
        Patient patient = Patient.builder()
                .firstName(patientRequest.getFirstName()).lastName(patientRequest.getLastName()).phone(patientRequest.getPhone())
                .emailId(patientRequest.getEmailId()).address(patientRequest.getAddress()).dob(patientRequest.getDob())
                .medicalHistory(patientRequest.getMedicalHistory())
                .build();

        this.patientRepo.save(patient);
        log.debug(Constant.PATIENT_NOT_FOUND + "{}", patient.getId());
        return ResponseMakerUtility.getPatientResponse(patient);
    }

    @Override
    public void deletePatient(UUID patientId) {
        Patient patient = patientRepo.findById(patientId).orElseThrow(
                () -> new NoSuchElementException(Constant.PATIENT_NOT_FOUND + patientId)
        );
        this.patientRepo.delete(patient);
    }

    @Override
    public PatientResponse updatePatient(PatientRequest patientRequest, UUID patientId) {
        Patient patient = patientRepo.findById(patientId).orElseThrow(
                () -> new NoSuchElementException(Constant.PATIENT_NOT_FOUND + patientId)
        );

        patient.setAddress(patientRequest.getAddress());
        patient.setDob(patientRequest.getDob());
        patient.setPhone(patientRequest.getPhone());
        patient.setFirstName(patientRequest.getFirstName());
        patient.setLastName(patientRequest.getLastName());
        patient.setEmailId(patientRequest.getEmailId());
        patient.setMedicalHistory(patientRequest.getMedicalHistory());

        return ResponseMakerUtility.getPatientResponse(patient);
    }

    @Override
    public Patient getPatientById(UUID patientId) {
        return patientRepo.findById(patientId).orElseThrow(
                () -> new NoSuchElementException(Constant.PATIENT_NOT_FOUND + patientId)
        );
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }
}

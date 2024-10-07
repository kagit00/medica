package com.medica.medicamanagement.doctor_service.service;

import com.medica.dto.DoctorApprovalResponse;
import com.medica.dto.DoctorResponse;
import com.medica.medicamanagement.doctor_service.dto.DoctorRequest;

import java.util.List;
import java.util.UUID;

public interface DoctorService {
    List<DoctorResponse> getAllDoctors();
    DoctorResponse getDoctorById(UUID id);
    DoctorResponse createDoctor(DoctorRequest doctorRequest);
    DoctorResponse updateDoctor(UUID id, DoctorRequest request);
}

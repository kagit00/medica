package com.medica.medicamanagement.doctor_service.service;

import com.medica.dto.DoctorApprovalResponse;
import com.medica.dto.DoctorResponse;
import com.medica.medicamanagement.doctor_service.dto.DoctorRequest;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DoctorService {
    Mono<List<DoctorResponse>> getAllDoctors();
    Mono<DoctorResponse> getDoctorById(UUID id);
    Mono<Map<String, Object>> createDoctor(DoctorRequest doctorRequest);
    Mono<DoctorResponse> updateDoctor(UUID id, DoctorRequest request);
    Mono<Void> deleteDoctor(UUID doctorId);
}

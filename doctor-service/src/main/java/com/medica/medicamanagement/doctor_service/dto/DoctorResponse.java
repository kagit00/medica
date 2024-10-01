package com.medica.medicamanagement.doctor_service.dto;

import com.medica.medicamanagement.doctor_service.model.DoctorAvailability;
import com.medica.medicamanagement.doctor_service.model.Specialization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DoctorResponse {
    private UUID id;
    private String name;
    private SpecializationResponse specialization;
    private String phone;
    private String email;
    private List<DoctorAvailabilityResponse> availabilities;
}

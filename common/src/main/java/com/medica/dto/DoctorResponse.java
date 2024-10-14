package com.medica.dto;

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
    private String username;
    private String firstName;
    private String lastName;
    private String fee;
    private int age;
    private SpecializationResponse specialization;
    private String phone;
    private String email;
    private String address;
    private List<DoctorAvailabilityResponse> availabilities;
}

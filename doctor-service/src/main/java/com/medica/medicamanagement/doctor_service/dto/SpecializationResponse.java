package com.medica.medicamanagement.doctor_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SpecializationResponse {
    private UUID id;
    private String name;
    private String description;
}

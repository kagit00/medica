package com.medica.medicamanagement.doctor_service.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpecializationRequest {
    @Pattern(regexp = "^[A-Za-z ]{2,100}$", message = "Specialization must contain only letters and spaces, and be between 2 and 100 characters")
    private String name;
    private String description;
}

package com.medica.medicamanagement.doctor_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DoctorRequest {
    @Pattern(regexp = "^[A-Za-z ]{2,100}$", message = "Name must contain only letters and spaces, and be between 2 and 100 characters")
    private String name;
    private String fee;
    @Valid
    private SpecializationRequest specializationRequest;
    @Pattern(regexp = "\\d{10}", message = "Phone number should be 10 digits")
    private String phone;
    @Email(message = "Invalid email format")
    private String email;
    @NotNull(message = "Availabilities cannot be null.")
    @NotEmpty(message = "At least one availability is required.")
    @Valid
    private List<DoctorAvailabilityRequest> availabilities;
}

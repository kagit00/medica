package com.medica.medicamanagement.doctor_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
    @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username can only contain alphanumeric characters")
    private String username;

    @NotBlank(message = "First name cannot be blank")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'’\\- ]+$", message = "Invalid First Name")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ'’\\- ]+$", message = "Invalid Last Name")
    private String lastName;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^\\+?[0-9. ()-]{10,13}$", message = "Invalid phone number")
    private String phone;

    private int age;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    private String fee;
    @Valid
    private SpecializationRequest specializationRequest;
    @Email(message = "Invalid email format")
    private String email;
    @NotNull(message = "Availabilities cannot be null.")
    @NotEmpty(message = "At least one availability is required.")
    @Valid
    private List<DoctorAvailabilityRequest> availabilities;
}

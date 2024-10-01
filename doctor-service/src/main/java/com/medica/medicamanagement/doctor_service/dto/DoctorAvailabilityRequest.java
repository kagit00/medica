package com.medica.medicamanagement.doctor_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class DoctorAvailabilityRequest {
    @NotNull(message = "dayOfWeek cannot be null")
    @Min(value = 1, message = "Day of the week must be between 1 (Monday) and 7 (Sunday).")
    @Max(value = 7, message = "Day of the week must be between 1 (Monday) and 7 (Sunday).")
    private int dayOfWeek;
    private String startTime;
    private String endTime;
}

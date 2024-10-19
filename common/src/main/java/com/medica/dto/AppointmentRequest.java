package com.medica.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.Date;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AppointmentRequest {
    @NotNull(message = "Patient ID is required")
    private String patientId;
    @NotNull(message = "Doctor ID is required")
    private String doctorId;
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private Date appointmentDate;
    @NotNull(message = "Time range is required")
    @Valid
    private TimeRange timeRange;
    @NotBlank(message = "Status is required")
    private String status;
}

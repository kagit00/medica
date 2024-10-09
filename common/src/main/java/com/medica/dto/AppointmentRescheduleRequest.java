package com.medica.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AppointmentRescheduleRequest {
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private Date appointmentDate;
    @NotNull(message = "Time range is required")
    @Valid
    private TimeRange timeRange;
}

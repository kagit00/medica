package com.medica.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TimeRange {
    @NotNull(message = "Start time cannot be null")
    @Future(message = "Start time must be in the future")
    private String startTime;

    @NotNull(message = "End time cannot be null")
    @Future(message = "End time must be in the future")
    private String endTime;
}

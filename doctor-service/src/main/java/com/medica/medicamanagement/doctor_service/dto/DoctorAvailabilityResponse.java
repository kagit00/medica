package com.medica.medicamanagement.doctor_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DoctorAvailabilityResponse {
    private Long id;
    private int dayOfWeek;
    private String startTime;
    private String endTime;
}

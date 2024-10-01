package com.medica.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AppointmentResponse {
    private UUID id;
    private UUID patientId;
    private Date appointmentDate;
    private UUID doctorId;
    private String status;
    private String startTime;
    private String endTime;
}

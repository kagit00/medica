package com.medica.dto;

import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DoctorApprovalResponse {
    private UUID appointmentId;
    private String status;
    private String doctorComments;
}

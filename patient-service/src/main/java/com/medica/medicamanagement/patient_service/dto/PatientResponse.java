package com.medica.medicamanagement.patient_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class PatientResponse {
    private UUID id;
    private String firstName;
    private String lastName;
    private String emailId;
    private String phone;
    private Date dob;
    private String address;
    private String medicalHistory;
}

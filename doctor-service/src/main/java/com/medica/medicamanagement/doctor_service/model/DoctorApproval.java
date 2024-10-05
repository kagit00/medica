package com.medica.medicamanagement.doctor_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class DoctorApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private UUID appointmentId;
    @Column(nullable = false)
    private UUID doctorId;
    @Column(nullable = false)
    private String status;
    @Column(columnDefinition = "text")
    private String doctorComments;
    @Column(nullable = false, name = "created_at")
    private String createdAt;
    @Column(nullable = false, name = "updated_at")
    private String updatedAt;
}

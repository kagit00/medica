package com.medica.medicamanagement.appointment_service.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentManager {
    @Column(nullable = false, updatable = false, unique = true)
    private UUID userId;
    private String createdAt;
    @Column(nullable = false)
    private String updatedAt;
}

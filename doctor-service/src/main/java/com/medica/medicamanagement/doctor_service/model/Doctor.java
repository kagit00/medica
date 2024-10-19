package com.medica.medicamanagement.doctor_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "doctor")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String fee;
    private UUID userId;
    @ManyToOne
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;
    private String createdAt;
    @Column(nullable = false)
    private String updatedAt;
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<DoctorAvailability> availabilities;
}

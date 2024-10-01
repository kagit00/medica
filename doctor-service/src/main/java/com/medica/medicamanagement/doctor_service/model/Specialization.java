package com.medica.medicamanagement.doctor_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specialization {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^[A-Za-z ]{2,100}$", message = "Specialization must contain only letters and spaces, and be between 2 and 100 characters")
    private String name;
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "specialization")
    private List<Doctor> doctors;
}

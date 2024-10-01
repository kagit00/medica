package com.medica.medicamanagement.doctor_service.dao;

import com.medica.medicamanagement.doctor_service.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {
}

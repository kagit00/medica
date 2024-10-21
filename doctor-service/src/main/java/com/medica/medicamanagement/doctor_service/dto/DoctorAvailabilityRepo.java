package com.medica.medicamanagement.doctor_service.dto;

import com.medica.medicamanagement.doctor_service.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorAvailabilityRepo extends JpaRepository<DoctorAvailability, Long> {
}

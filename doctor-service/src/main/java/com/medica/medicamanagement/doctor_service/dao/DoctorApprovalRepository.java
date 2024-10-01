package com.medica.medicamanagement.doctor_service.dao;

import com.medica.medicamanagement.doctor_service.model.DoctorApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DoctorApprovalRepository extends JpaRepository<DoctorApproval, UUID> {
    DoctorApproval findByAppointmentId(UUID appointmentId);
}

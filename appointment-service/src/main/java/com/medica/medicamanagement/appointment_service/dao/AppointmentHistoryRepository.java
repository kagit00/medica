package com.medica.medicamanagement.appointment_service.dao;

import com.medica.medicamanagement.appointment_service.model.AppointmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AppointmentHistoryRepository extends JpaRepository<AppointmentHistory, UUID> {
}

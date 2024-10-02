package com.medica.medicamanagement.appointment_service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "patient_id", nullable = false)
    private UUID patientId;
    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;
    @Column(name = "appointment_date", nullable = false)
    private Date appointmentDate;
    @Column(name = "start_time", nullable = false)
    private String startTime;
    @Column(name = "end_time", nullable = false)
    private String endTime;
    @Column(name = "status", nullable = false)
    private String status;
    @Column(name = "appointment_description", columnDefinition = "text")
    private String appointmentDescription;
    @Column(nullable = false, name = "created_at")
    private String createdAt;
    @Column(nullable = false, name = "updated_at")
    private String updatedAt;
}

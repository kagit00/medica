package com.medica.medicamanagement.doctor_service.controller;

import com.medica.dto.DoctorApprovalResponse;
import com.medica.medicamanagement.doctor_service.service.AppointmentStatusUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/doctors/appointments")
@RequiredArgsConstructor
public class AppointmentStatusUpdateController {
    private final AppointmentStatusUpdateService appointmentStatusUpdateService;

    @PutMapping("/{appointmentId}/{status}")
    public ResponseEntity<DoctorApprovalResponse> updateAppointmentStatus(@PathVariable("appointmentId") String appointmentId, @PathVariable("status") String status) {
        return ResponseEntity.ok(this.appointmentStatusUpdateService.updateAppointmentStatus(UUID.fromString(appointmentId), status));
    }
}

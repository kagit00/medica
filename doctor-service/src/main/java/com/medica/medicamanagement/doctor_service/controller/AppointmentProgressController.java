package com.medica.medicamanagement.doctor_service.controller;

import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorApprovalResponse;
import com.medica.medicamanagement.doctor_service.service.AppointmentProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors/appointments")
@RequiredArgsConstructor
public class AppointmentProgressController {
    private final AppointmentProgressService appointmentProgressService;

    @PutMapping("/{appointmentId}/{status}")
    public ResponseEntity<DoctorApprovalResponse> updateAppointmentStatus(@PathVariable("appointmentId") String appointmentId, @PathVariable("status") String status) {
        return ResponseEntity.ok(this.appointmentProgressService.updateAppointmentStatus(UUID.fromString(appointmentId), status));
    }
}

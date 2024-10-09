package com.medica.medicamanagement.patient_service.controller;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentRescheduleRequest;
import com.medica.dto.NotificationResponse;
import com.medica.medicamanagement.patient_service.service.AppointmentProgressService;
import com.medica.medicamanagement.patient_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients/appointments")
@AllArgsConstructor
public class AppointmentProgressController {
    private final PatientService patientService;
    private final AppointmentProgressService appointmentProgressService;


    @PostMapping(value = "/request-appointment", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> requestAppointment(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(this.appointmentProgressService.requestForAppointment(request));
    }

    @PutMapping(value = "/cancel/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> cancelAppointment(@PathVariable("appointmentId") String appointmentId) {
        return ResponseEntity.ok(this.appointmentProgressService.cancelAppointment(appointmentId));
    }

    @PutMapping(value = "/reschedule/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> rescheduleAppointment(@PathVariable("appointmentId") String appointmentId, @RequestBody @Valid AppointmentRescheduleRequest appointmentRescheduleRequest) {
        return ResponseEntity.ok(this.appointmentProgressService.rescheduleAppointment(appointmentId, appointmentRescheduleRequest));
    }
}

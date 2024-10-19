package com.medica.medicamanagement.patient_service.controller;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentRescheduleRequest;
import com.medica.dto.AppointmentResponse;
import com.medica.dto.NotificationResponse;
import com.medica.medicamanagement.patient_service.service.AppointmentProgressService;
import com.medica.medicamanagement.patient_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * The type Appointment progress controller.
 */
@RestController
@RequestMapping("/api/patients/appointments")
@AllArgsConstructor
public class AppointmentProgressController {
    private final PatientService patientService;
    private final AppointmentProgressService appointmentProgressService;


    /**
     * Request appointment response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @PostMapping(value = "/request-appointment", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> requestAppointment(@RequestBody AppointmentRequest request) {
        return ResponseEntity.ok(this.appointmentProgressService.requestForAppointment(request));
    }

    /**
     * Cancel appointment response entity.
     *
     * @param appointmentId the appointment id
     * @return the response entity
     */
    @PutMapping(value = "/cancel/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> cancelAppointment(@PathVariable("appointmentId") String appointmentId) {
        return ResponseEntity.ok(this.appointmentProgressService.cancelAppointment(appointmentId));
    }

    /**
     * Reschedule appointment response entity.
     *
     * @param appointmentId                the appointment id
     * @param appointmentRescheduleRequest the appointment reschedule request
     * @return the response entity
     */
    @PutMapping(value = "/reschedule/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> rescheduleAppointment(@PathVariable("appointmentId") String appointmentId, @RequestBody @Valid AppointmentRescheduleRequest appointmentRescheduleRequest) {
        return ResponseEntity.ok(this.appointmentProgressService.rescheduleAppointment(appointmentId, appointmentRescheduleRequest));
    }
}

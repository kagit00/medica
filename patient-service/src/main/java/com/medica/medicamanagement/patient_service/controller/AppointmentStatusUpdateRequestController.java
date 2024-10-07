package com.medica.medicamanagement.patient_service.controller;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.NotificationResponse;
import com.medica.medicamanagement.patient_service.service.AppointmentStatusUpdateRequestService;
import com.medica.medicamanagement.patient_service.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients/appointments")
@AllArgsConstructor
public class AppointmentStatusUpdateRequestController {
    private final PatientService patientService;
    private final AppointmentStatusUpdateRequestService appointmentStatusUpdateRequestService;


    @PostMapping(value = "/request-appointment", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> requestAppointment(@RequestBody AppointmentRequest request) {
        return new ResponseEntity<>(this.appointmentStatusUpdateRequestService.requestForAppointment(request), HttpStatusCode.valueOf(200));
    }

    @PutMapping(value = "/cancel/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> cancelAppointment(@PathVariable("appointmentId") String appointmentId) {
        return new ResponseEntity<>(this.appointmentStatusUpdateRequestService.cancelAppointment(appointmentId), HttpStatusCode.valueOf(200));
    }
}

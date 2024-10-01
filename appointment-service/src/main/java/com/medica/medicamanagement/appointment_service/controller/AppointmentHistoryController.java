package com.medica.medicamanagement.appointment_service.controller;

import com.medica.medicamanagement.appointment_service.model.AppointmentHistory;
import com.medica.medicamanagement.appointment_service.service.AppointmentHistoryServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments/{appointmentId}/history")
@RequiredArgsConstructor
public class AppointmentHistoryController {
    private final AppointmentHistoryServiceImplementation appointmentHistoryService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AppointmentHistory>> getAppointmentHistory(@PathVariable String appointmentId) {
        List<AppointmentHistory> history = appointmentHistoryService.getHistoryByAppointmentId(UUID.fromString(appointmentId));
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppointmentHistory> createAppointmentHistory(@PathVariable String appointmentId, @RequestBody AppointmentHistory history) {
        AppointmentHistory createdHistory = appointmentHistoryService.createHistory(UUID.fromString(appointmentId), history);
        return new ResponseEntity<>(createdHistory, HttpStatus.CREATED);
    }
}

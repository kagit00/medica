package com.medica.medicamanagement.patient_service.controller;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.NotificationResponse;
import com.medica.medicamanagement.patient_service.dto.PatientRequest;
import com.medica.medicamanagement.patient_service.dto.PatientResponse;
import com.medica.medicamanagement.patient_service.models.Patient;
import com.medica.medicamanagement.patient_service.service.PatientService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@AllArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientResponse> createPatient(@RequestBody PatientRequest patient) {
        return new ResponseEntity<>(this.patientService.createPatient(patient), HttpStatusCode.valueOf(201));
    }

    @PutMapping(value = "/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientResponse> updatePatient(@RequestBody PatientRequest patientRequest, @PathVariable("patientId") String patientId) {
        return new ResponseEntity<>(this.patientService.updatePatient(patientRequest, UUID.fromString(patientId)), HttpStatusCode.valueOf(200));
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Patient>> getAllPatients() {
        return new ResponseEntity<>(this.patientService.getAllPatients(), HttpStatusCode.valueOf(200));
    }

    @GetMapping(value = "/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Patient> getPatientById(@PathVariable("patientId") String patientId) {
        return new ResponseEntity<>(this.patientService.getPatientById(UUID.fromString(patientId)), HttpStatusCode.valueOf(200));
    }

    @DeleteMapping(value = "/patient/{patientId}")
    public ResponseEntity<?> deletePatientById(@PathVariable("patientId") String patientId) {
        this.patientService.deletePatient(UUID.fromString(patientId));
        return new ResponseEntity<>(HttpStatusCode.valueOf(200));
    }

    @PostMapping(value = "/request-appointment", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> requestAppointment(@RequestBody AppointmentRequest request) {
        return new ResponseEntity<>(this.patientService.requestForAppointment(request), HttpStatusCode.valueOf(200));
    }

    @PutMapping(value = "/cancel-appointment/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponse> cancelAppointment(@PathVariable("appointmentId") String appointmentId) {
        return new ResponseEntity<>(this.patientService.cancelAppointment(appointmentId), HttpStatusCode.valueOf(200));
    }
}

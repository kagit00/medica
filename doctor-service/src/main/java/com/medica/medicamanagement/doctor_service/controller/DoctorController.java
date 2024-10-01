package com.medica.medicamanagement.doctor_service.controller;

import com.medica.dto.DoctorApprovalResponse;
import com.medica.medicamanagement.doctor_service.dto.DoctorRequest;
import com.medica.medicamanagement.doctor_service.dto.DoctorResponse;
import com.medica.medicamanagement.doctor_service.service.DoctorServiceImplementation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorServiceImplementation doctorService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        List<DoctorResponse> responses = doctorService.getAllDoctors();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable String id) {
        DoctorResponse response = doctorService.getDoctorById(UUID.fromString(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DoctorResponse> createDoctor(@RequestBody @Valid DoctorRequest doctor) {
        DoctorResponse response = doctorService.createDoctor(doctor);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/doctor/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DoctorResponse> createDoctor(@PathVariable String id, @RequestBody @Valid DoctorRequest doctor) {
        DoctorResponse response = doctorService.updateDoctor(UUID.fromString(id), doctor);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/appointment/{appointmentId}/{status}")
    public ResponseEntity<DoctorApprovalResponse> approveSingleAppointment(@PathVariable("appointmentId") String appointmentId, @PathVariable("status") String status) {
        return ResponseEntity.ok(this.doctorService.approveSingleAppointment(UUID.fromString(appointmentId), status));
    }
}

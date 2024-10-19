package com.medica.medicamanagement.doctor_service.controller;

import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorResponse;
import com.medica.medicamanagement.doctor_service.dto.DoctorRequest;
import com.medica.medicamanagement.doctor_service.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    /**
     * Create a new doctor.
     *
     * @param doctor the doctor details
     * @return the response entity
     */
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String, Object>>> createDoctor(@RequestBody @Valid DoctorRequest doctor) {
        return this.doctorService.createDoctor(doctor)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    /**
     * Update doctor details by ID.
     *
     * @param id     the doctor ID
     * @param doctor the doctor details
     * @return the response entity
     */
    @PutMapping(value = "/doctor/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<DoctorResponse>> updateDoctor(@PathVariable String id, @RequestBody @Valid DoctorRequest doctor) {
        return this.doctorService.updateDoctor(UUID.fromString(id), doctor)
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    /**
     * Gets all doctors.
     *
     * @return the list of doctors
     */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<DoctorResponse>>> getAllDoctors() {
        return this.doctorService.getAllDoctors()
                .map(ResponseEntity::ok)
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    /**
     * Gets a doctor by ID.
     *
     * @param id the doctor ID
     * @return the doctor details
     */
    @GetMapping(value = "/doctor/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<DoctorResponse>> getDoctorById(@PathVariable String id) {
        return this.doctorService.getDoctorById(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    /**
     * Delete a doctor by ID.
     *
     * @param doctorId the doctor ID
     * @return the response entity
     */
    @DeleteMapping(value = "/doctor/{doctorId}")
    public Mono<ResponseEntity<Void>> deleteDoctorById(@PathVariable("doctorId") String doctorId) {
        return this.doctorService.deleteDoctor(UUID.fromString(doctorId))
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}

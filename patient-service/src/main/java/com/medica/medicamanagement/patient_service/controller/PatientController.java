package com.medica.medicamanagement.patient_service.controller;

import com.medica.dto.PatientRequest;
import com.medica.dto.PatientResponse;
import com.medica.medicamanagement.patient_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type Patient controller.
 */
@RestController
@RequestMapping("/api/patients")
@AllArgsConstructor
public class PatientController {
    private final PatientService patientService;

    /**
     * Create patient response entity.
     *
     * @param patient the patient
     * @return the response entity
     */
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String, Object>>> createPatient(@RequestBody @Valid  PatientRequest patient) {
        return this.patientService.createPatient(patient)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    /**
     * Update patient response entity.
     *
     * @param patientRequest the patient request
     * @param patientId      the patient id
     * @return the response entity
     */
    @PutMapping(value = "/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PatientResponse>> updatePatient(@RequestBody @Valid PatientRequest patientRequest, @PathVariable("patientId") String patientId) {
        return this.patientService.updatePatient(patientRequest, UUID.fromString(patientId))
                .map(ResponseEntity::ok)
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    /**
     * Gets all patients.
     *
     * @return the all patients
     */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<PatientResponse>>> getAllPatients() {
        return this.patientService.getAllPatients()
                .map(ResponseEntity::ok)
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

    /**
     * Gets patient by id.
     *
     * @param patientId the patient id
     * @return the patient by id
     */
    @GetMapping(value = "/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PatientResponse>> getPatientById(@PathVariable("patientId") String patientId) {
        return this.patientService.getPatientById(UUID.fromString(patientId))
                .map(ResponseEntity::ok)
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));  // Handle errors
    }

    /**
     * Delete patient by id mono.
     *
     * @param patientId the patient id
     * @return the mono
     */
    @DeleteMapping(value = "/patient/{patientId}")
    public Mono<ResponseEntity<Void>> deletePatientById(@PathVariable("patientId") String patientId) {
        return this.patientService.deletePatient(UUID.fromString(patientId))
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}

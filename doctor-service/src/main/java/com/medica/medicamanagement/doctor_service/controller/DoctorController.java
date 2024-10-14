package com.medica.medicamanagement.doctor_service.controller;

import com.medica.dto.DoctorResponse;
import com.medica.medicamanagement.doctor_service.dto.DoctorRequest;
import com.medica.medicamanagement.doctor_service.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<List<DoctorResponse>>> getAllDoctors() {
        return new ResponseEntity<>(this.doctorService.getAllDoctors(), HttpStatusCode.valueOf(200));
    }

    @GetMapping(value = "/doctor/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<DoctorResponse>> getDoctorById(@PathVariable String id) {
        return new ResponseEntity<>(this.doctorService.getDoctorById(UUID.fromString(id)), HttpStatusCode.valueOf(200));
    }

    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<DoctorResponse>> createDoctor(@RequestBody @Valid DoctorRequest doctor) {
        return new ResponseEntity<>(this.doctorService.createDoctor(doctor), HttpStatusCode.valueOf(201));
    }

    @PostMapping(value = "/doctor/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Mono<DoctorResponse>> updateDoctor(@PathVariable String id, @RequestBody @Valid DoctorRequest doctor) {
        return new ResponseEntity<>(this.doctorService.updateDoctor(UUID.fromString(id), doctor), HttpStatusCode.valueOf(200));
    }

    @DeleteMapping(value = "/doctor/{doctorId}")
    public Mono<ResponseEntity<Void>> deleteDoctorById(@PathVariable("doctorId") String doctorId) {
        return this.doctorService.deleteDoctor(UUID.fromString(doctorId))
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorResume(error -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }
}

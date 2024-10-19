package com.medica.medicamanagement.appointment_service.controller;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentResponse;
import com.medica.medicamanagement.appointment_service.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * The type Appointment controller.
 */
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    /**
     * Create appointment response entity.
     *
     * @param request the request
     * @return the response entity
     */
    @PostMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody @Valid AppointmentRequest request) {
        AppointmentResponse response = appointmentService.createAppointment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Gets all appointments.
     *
     * @return the all appointments
     */
    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<AppointmentResponse> responses = appointmentService.getAllAppointments();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /**
     * Gets appointment by id.
     *
     * @param id the id
     * @return the appointment by id
     */
    @GetMapping(value = "/appointment/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable String id) {
        AppointmentResponse response = appointmentService.getAppointmentById(UUID.fromString(id));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update appointment response entity.
     *
     * @param id      the id
     * @param request the request
     * @return the response entity
     */
    @PutMapping("/appointment/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(@PathVariable String id, @RequestBody @Valid AppointmentRequest request) {
        AppointmentResponse response = appointmentService.updateAppointment(UUID.fromString(id), request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete appointment response entity.
     *
     * @param id the id
     * @return the response entity
     */
    @DeleteMapping("/appointment/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(UUID.fromString(id));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Gets appointment info.
     *
     * @param patientId the patient id
     * @param date      the date
     * @param startTime the start time
     * @param endTime   the end time
     * @return the appointment info
     */
    @GetMapping(value = "/patient/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppointmentResponse> getAppointmentInfo(@PathVariable() String patientId,
                                                                  @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") String date,
                                                                  @RequestParam String startTime,
                                                                  @RequestParam String endTime) {
        return ResponseEntity.ok(this.appointmentService.getAppointmentInfo(
                UUID.fromString(patientId), date, startTime, endTime)
        );
    }

    /**
     * Gets all appointments.
     *
     * @param patientId the patient id
     * @return the all appointments
     */
    @GetMapping(value = "/patient/{patientId}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments(@PathVariable("patientId") String patientId) {
        return ResponseEntity.ok(this.appointmentService.getAllAppointments(UUID.fromString(patientId)));
    }

    /**
     * Gets appointment info for doctor.
     *
     * @param doctorId  the doctor id
     * @param date      the date
     * @param startTime the start time
     * @param endTime   the end time
     * @return the appointment info for doctor
     */
    @GetMapping(value = "/doctor/{doctorId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AppointmentResponse> getAppointmentInfoForDoctor(@PathVariable() String doctorId,
                                                                  @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") String date,
                                                                  @RequestParam String startTime,
                                                                  @RequestParam String endTime) {
        return ResponseEntity.ok(this.appointmentService.getAppointmentInfoForDoctor(
                UUID.fromString(doctorId), date, startTime, endTime)
        );
    }

    /**
     * Gets all appointments for doctor.
     *
     * @param doctorId the doctor id
     * @return the all appointments for doctor
     */
    @GetMapping(value = "/doctor/{doctorId}/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AppointmentResponse>> getAllAppointmentsForDoctor(@PathVariable("doctorId") String doctorId) {
        return ResponseEntity.ok(this.appointmentService.getAllAppointmentsForDoctor(UUID.fromString(doctorId)));
    }
}

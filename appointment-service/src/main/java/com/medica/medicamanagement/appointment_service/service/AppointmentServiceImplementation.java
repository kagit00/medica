package com.medica.medicamanagement.appointment_service.service;

import com.medica.dto.*;
import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.medicamanagement.appointment_service.util.ResponseMakerUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImplementation implements AppointmentService {
    private final AppointmentRepository appointmentRepository;

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Appointment appointment = Appointment.builder()
                .patientId(UUID.fromString(request.getPatientId())).doctorId(UUID.fromString(request.getDoctorId()))
                .appointmentDate(request.getAppointmentDate()).status(request.getStatus())
                .startTime(request.getTimeRange().getStartTime()).endTime(request.getTimeRange().getEndTime())
                .build();

        this.appointmentRepository.save(appointment);
        return ResponseMakerUtility.getAppointmentResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream().map(ResponseMakerUtility::getAppointmentResponse).toList();
    }

    @Override
    public AppointmentResponse getAppointmentById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Appointment Not Found With Id: " + id)
        );

        return ResponseMakerUtility.getAppointmentResponse(appointment);
    }

    @Override
    public AppointmentResponse updateAppointment(UUID id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow();

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getTimeRange().getStartTime());
        appointment.setEndTime(request.getTimeRange().getEndTime());
        appointment.setDoctorId(UUID.fromString(request.getDoctorId()));
        appointment.setPatientId(UUID.fromString(request.getPatientId()));
        appointment.setStatus(request.getStatus());

        this.appointmentRepository.save(appointment);
        return ResponseMakerUtility.getAppointmentResponse(appointment);
    }

    @Override
    public void deleteAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Appointment Not Found With Id: " + id)
        );
        this.appointmentRepository.delete(appointment);
    }

    @Override
    public AppointmentResponse getAppointmentInfo(UUID patientId, String date, String startTime, String endTime) {
        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Optional<Appointment> appointmentOpt = appointmentRepository.findByPatientIdAndDateAndTimeRange(patientId, parsedDate.toString(), startTime, endTime);

        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            return ResponseMakerUtility.getAppointmentResponse(appointment);
        }

        throw new NoSuchElementException("No Appointment Matched As Per Criteria");
    }

    @Override
    public List<AppointmentResponse> getAllAppointments(UUID patientId) {
        List<Appointment> appointments = this.appointmentRepository.findByPatientId(patientId).orElseThrow(
                () -> new NoSuchElementException("No Appointments Found For " + patientId)
        );

        return appointments.stream().map(ResponseMakerUtility::getAppointmentResponse).toList();
    }

    @Override
    public AppointmentResponse getAppointmentInfoForDoctor(UUID doctorId, String date, String startTime, String endTime) {
        LocalDate parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Optional<Appointment> appointmentOpt = appointmentRepository.findByDoctorIdAndDateAndTimeRange(doctorId, parsedDate.toString(), startTime, endTime);

        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            return ResponseMakerUtility.getAppointmentResponse(appointment);
        }

        throw new NoSuchElementException("No Appointment Matched As Per Criteria");
    }

    @Override
    public List<AppointmentResponse> getAllAppointmentsForDoctor(UUID doctorId) {
        List<Appointment> appointments = this.appointmentRepository.findByDoctorId(doctorId).orElseThrow(
                () -> new NoSuchElementException("No Appointments Found For " + doctorId)
        );

        return appointments.stream().map(ResponseMakerUtility::getAppointmentResponse).toList();
    }
}

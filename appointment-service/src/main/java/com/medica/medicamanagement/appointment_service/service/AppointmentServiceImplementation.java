package com.medica.medicamanagement.appointment_service.service;

import com.medica.dto.*;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.medicamanagement.appointment_service.util.ResponseMakerUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImplementation implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId()).doctorId(request.getDoctorId())
                .appointmentDate(request.getAppointmentDate()).status(request.getStatus())
                .startTime(request.getTimeRange().getStartTime())
                .endTime(request.getTimeRange().getEndTime())
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
        appointment.setDoctorId(request.getDoctorId());
        appointment.setPatientId(request.getPatientId());
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
}

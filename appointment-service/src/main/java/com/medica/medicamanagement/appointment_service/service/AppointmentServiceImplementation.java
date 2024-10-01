package com.medica.medicamanagement.appointment_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.AppointmentRequest;
import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorApprovalResponse;
import com.medica.dto.TimeRange;
import com.medica.medicamanagement.appointment_service.client.DoctorServiceClientImplementation;
import com.medica.medicamanagement.appointment_service.dao.AppointmentRepository;
import com.medica.medicamanagement.appointment_service.model.Appointment;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImplementation implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorServiceClientImplementation doctorService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper om = new ObjectMapper();
    private static final String TOPIC = "appointment_response_by_appointment_setters";

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        Appointment appointment = Appointment.builder()
                .patientId(request.getPatientId()).doctorId(request.getDoctorId())
                .appointmentDate(request.getAppointmentDate()).status(request.getStatus())
                .startTime(request.getTimeRange().getStartTime())
                .endTime(request.getTimeRange().getEndTime())
                .build();

        this.appointmentRepository.save(appointment);
        return mapToResponse(appointment);
    }

    @Override
    public List<AppointmentResponse> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream().map(this::mapToResponse).toList();
    }

    @Override
    public AppointmentResponse getAppointmentById(UUID id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Appointment Not Found With Id: " + id)
        );

        return mapToResponse(appointment);
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
        return mapToResponse(appointment);
    }

    @Override
    public void deleteAppointment(UUID id) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("Appointment Not Found With Id: " + id)
        );
        this.appointmentRepository.delete(appointment);
    }

    @KafkaListener(topics = "appointment_request_by_patient", groupId = "appointment-service-group")
    public void requestDoctorForAppointment(String appointmentRequest) {
        try {
            AppointmentRequest request = om.readValue(appointmentRequest, AppointmentRequest.class);
            this.doctorService.getDoctorById(request.getDoctorId().toString());

            boolean isTimeSlotAlreadyTakenByPatient = this.appointmentRepository.existsByTimeRangePatient(request.getPatientId(),
                    request.getAppointmentDate(), request.getTimeRange().getStartTime(), request.getTimeRange().getEndTime());

            if (isTimeSlotAlreadyTakenByPatient) {
                kafkaTemplate.send(TOPIC, "Invalid Appointment Request. Time slot already taken by patient");
                return;
            }

            boolean isTimeSlotAlreadyTakenByDoctor = this.appointmentRepository.existsByTimeRangeDoctor(request.getDoctorId(),
                    request.getAppointmentDate(), request.getTimeRange().getStartTime(), request.getTimeRange().getEndTime());

            if (isTimeSlotAlreadyTakenByDoctor) {
                kafkaTemplate.send(TOPIC, "Invalid Appointment Request. Time slot already taken by doctor");
                return;
            }

            Appointment appointment = Appointment.builder()
                    .patientId(request.getPatientId()).doctorId(request.getDoctorId()).appointmentDate(request.getAppointmentDate())
                    .status(request.getStatus()).startTime(request.getTimeRange().getStartTime()).endTime(request.getTimeRange().getEndTime())
                    .build();

            this.appointmentRepository.save(appointment);
            DoctorApprovalResponse doctorApprovalResponse = DoctorApprovalResponse.builder().appointmentId(appointment.getId()).build();
            kafkaTemplate.send(TOPIC, BasicUtility.stringifyObject(doctorApprovalResponse));
            log.info("Appointment Request is valid. Forwarding to Doctor for Approval.");

        } catch (Exception e) {
            kafkaTemplate.send(TOPIC, e.getMessage());
        }
    }

    @KafkaListener(topics = "appointment_response_by_doctor", groupId = "appointment-service-group")
    public void respondToAppointmentRequest(String doctorApprovalResponse) {
        try {
            String responseMessage = "";
            DoctorApprovalResponse approvalResponse = om.readValue(doctorApprovalResponse, DoctorApprovalResponse.class);

            UUID appointmentId = approvalResponse.getAppointmentId();
            Appointment appointment = this.appointmentRepository.findById(appointmentId).orElseThrow(
                    () -> new NoSuchElementException("Appointment Not Found.")
            );

            if (AppointmentStatus.SCHEDULED.name().equals(approvalResponse.getStatus())) {
                appointment.setStatus(approvalResponse.getStatus());
                responseMessage = "Doctor Approval Received Successfully";

            } else if (AppointmentStatus.REJECTED.name().equals(approvalResponse.getStatus())) {
                appointment.setStatus(approvalResponse.getStatus());
                responseMessage = "Doctor Rejected The Appointment.";

            } else if (AppointmentStatus.HOLD.name().equals(approvalResponse.getStatus())) {
                appointment.setStatus(approvalResponse.getStatus());
                this.appointmentRepository.save(appointment);
                return;

            } else
                return;

            this.appointmentRepository.save(appointment);
            kafkaTemplate.send(TOPIC, responseMessage);

        } catch (Exception e) {
            kafkaTemplate.send(TOPIC, e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkPendingAppointments() {
        List<Appointment> pendingAppointments = appointmentRepository.findByStatus(AppointmentStatus.HOLD.name());

        int batchSize = 5;
        int processedCount = 0;

        for (Appointment appointment : pendingAppointments) {
            if (processedCount >= batchSize) {
                break;
            }

            kafkaTemplate.send("appointment-status-update-retry", String.valueOf(appointment.getId()));
            processedCount++;
        }
    }

    private AppointmentResponse mapToResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId()).doctorId(appointment.getDoctorId())
                .appointmentDate(appointment.getAppointmentDate()).status(appointment.getStatus())
                .startTime(appointment.getStartTime()).endTime(appointment.getEndTime())
                .build();
    }
}

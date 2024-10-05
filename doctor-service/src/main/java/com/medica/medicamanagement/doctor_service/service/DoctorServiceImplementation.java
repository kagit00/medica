package com.medica.medicamanagement.doctor_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medica.dto.DoctorApprovalResponse;
import com.medica.medicamanagement.doctor_service.dao.DoctorApprovalRepository;
import com.medica.medicamanagement.doctor_service.dao.DoctorAvailabilityRepository;
import com.medica.medicamanagement.doctor_service.dao.DoctorRepository;
import com.medica.medicamanagement.doctor_service.dao.SpecializationRepository;
import com.medica.medicamanagement.doctor_service.dto.DoctorAvailabilityResponse;
import com.medica.medicamanagement.doctor_service.dto.DoctorRequest;
import com.medica.medicamanagement.doctor_service.dto.DoctorResponse;
import com.medica.medicamanagement.doctor_service.dto.SpecializationResponse;
import com.medica.medicamanagement.doctor_service.model.Doctor;
import com.medica.medicamanagement.doctor_service.model.DoctorApproval;
import com.medica.medicamanagement.doctor_service.model.DoctorAvailability;
import com.medica.medicamanagement.doctor_service.model.Specialization;
import com.medica.medicamanagement.doctor_service.utils.DefaultValuesPopulator;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImplementation implements DoctorService {
    private final DoctorApprovalRepository doctorApprovalRepository;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorAvailabilityRepository doctorAvailabilityRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper om = new ObjectMapper();
    private static final String TOPIC = "appointment_response_by_doctor";

    @Override
    public List<DoctorResponse> getAllDoctors() {
        List<Doctor> doctors =  doctorRepository.findAll();
        return doctors.stream().map(this::mapToResponse).toList();
    }

    @Override
    public DoctorResponse getDoctorById(UUID id) {
        Doctor doctor = this.doctorRepository.findById(id).orElse(null);
        return Objects.isNull(doctor)? null : mapToResponse(doctor);
    }

    @Override
    public DoctorResponse createDoctor(DoctorRequest request) {
        Specialization specialization = Specialization.builder().name(request.getSpecializationRequest().getName())
                .description(request.getSpecializationRequest().getDescription())
                .build();

        Doctor doctor = Doctor.builder()
                .email(request.getEmail()).phone(request.getPhone()).name(request.getName()).fee(request.getFee())
                .specialization(specialization).createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                .build();

        List<DoctorAvailability> doctorAvailabilities = request.getAvailabilities().stream()
                .map(availabilityRequest -> DoctorAvailability.builder()
                        .dayOfWeek(availabilityRequest.getDayOfWeek()).startTime(availabilityRequest.getStartTime())
                        .endTime(availabilityRequest.getEndTime()).createdAt(DefaultValuesPopulator.getCurrentTimestamp())
                        .doctor(doctor).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                        .build())
                .toList();

        doctor.setAvailabilities(doctorAvailabilities);
        this.specializationRepository.save(specialization);
        this.doctorRepository.save(doctor);
        return mapToResponse(doctor);
    }

    @Override
    public DoctorResponse updateDoctor(UUID id, DoctorRequest request) {
        Doctor doctor = this.doctorRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("No Doctor Found With Id: " + id)
        );

        Specialization specialization = Specialization.builder().name(request.getSpecializationRequest().getName())
                .description(request.getSpecializationRequest().getDescription())
                .build();

        doctor.setName(request.getName());
        doctor.setPhone(request.getPhone());
        doctor.setEmail(request.getEmail());
        doctor.setSpecialization(specialization);
        doctor.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

        this.doctorRepository.save(doctor);
        return mapToResponse(doctor);
    }

    @KafkaListener(topics = "appointment_response_by_appointment_setters", groupId = "doctor-service-group")
    public void respondToAppointmentRequest(String doctorApprovalResponse) {
        try {
            DoctorApprovalResponse approvalResponse = om.readValue(doctorApprovalResponse, DoctorApprovalResponse.class);
            DoctorApproval approval = DoctorApproval.builder()
                    .doctorId(approvalResponse.getDoctorId())
                    .appointmentId(approvalResponse.getAppointmentId()).status(AppointmentStatus.PENDING.name()).doctorComments("NA")
                    .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                    .build();

            doctorApprovalRepository.save(approval);
        } catch (Exception e) {
            kafkaTemplate.send(TOPIC, "There is an issue with receiving appointment request. Reason: " + e.getMessage());
        }
    }

    private void updateAppointmentStatus(DoctorApproval doctorApproval, String status) {
        switch (status) {
            case "APPROVED":
                doctorApproval.setDoctorComments("Appointment Approved");
                doctorApproval.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
                doctorApproval.setStatus(AppointmentStatus.APPROVED.name());
                break;

            case "REJECTED":
                doctorApproval.setDoctorComments("Appointment Rejected");
                doctorApproval.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());
                doctorApproval.setStatus(AppointmentStatus.REJECTED.name());
                break;

            default:
                doctorApproval.setDoctorComments("Appointment Hold");
                doctorApproval.setStatus(AppointmentStatus.PENDING.name());
                log.warn("Received an unexpected status: {}", status);
                break;
        }
    }

    @Override
    public DoctorApprovalResponse approveSingleAppointment(UUID appointmentId, String status) {
        DoctorApproval doctorApproval = this.doctorApprovalRepository.findByAppointmentId(appointmentId);
        if (!AppointmentStatus.PENDING.name().equals(doctorApproval.getStatus())) {
            return null;
        }

        updateAppointmentStatus(doctorApproval, status);
        this.doctorApprovalRepository.save(doctorApproval);

        DoctorApprovalResponse doctorApprovalResponse = DoctorApprovalResponse.builder()
                        .appointmentId(appointmentId).doctorComments(doctorApproval.getDoctorComments())
                .doctorId(doctorApproval.getDoctorId()).status(doctorApproval.getStatus()).build();

        kafkaTemplate.send(TOPIC,
                BasicUtility.stringifyObject(doctorApprovalResponse) + " <> " +
                        BasicUtility.stringifyObject(getDoctorById(doctorApproval.getDoctorId()))
        );
        return doctorApprovalResponse;
    }

    @KafkaListener(topics = "appointment-status-update-retry", groupId = "doctor-service-group")
    public void handleAppointmentRetry(String appointmentId) {
        approveSingleAppointment(UUID.fromString(appointmentId), AppointmentStatus.PENDING.name());
    }

    @KafkaListener(topics = "appointment-cancelled-by-appointment-setters", groupId = "doctor-service-group")
    public void cancelAppointmentOnAppointmentSettersRequest(String appointmentId) {
        DoctorApproval doctorApproval = this.doctorApprovalRepository.findByAppointmentId(UUID.fromString(appointmentId));

        if (AppointmentStatus.REJECTED.name().equals(doctorApproval.getStatus())) return;

        doctorApproval.setStatus(AppointmentStatus.CANCELED.name());
        this.doctorApprovalRepository.save(doctorApproval);
    }

    private DoctorResponse mapToResponse(Doctor doctor) {
        SpecializationResponse specializationResponse = SpecializationResponse.builder().name(doctor.getSpecialization().getName())
                .description(doctor.getSpecialization().getDescription()).id(doctor.getSpecialization().getId())
                .build();

        List<DoctorAvailabilityResponse> doctorAvailabilityResponses = doctor.getAvailabilities().stream()
                .map(availabilityResponse -> DoctorAvailabilityResponse.builder()
                        .dayOfWeek(availabilityResponse.getDayOfWeek()).id(availabilityResponse.getId())
                        .startTime(String.valueOf(availabilityResponse.getStartTime())).endTime(String.valueOf(availabilityResponse.getEndTime()))
                        .build())
                .toList();

        return DoctorResponse.builder()
                .id(doctor.getId()).fee(doctor.getFee())
                .email(doctor.getEmail()).phone(doctor.getPhone()).name(doctor.getName())
                .specialization(specializationResponse).availabilities(doctorAvailabilityResponses)
                .build();
    }
}

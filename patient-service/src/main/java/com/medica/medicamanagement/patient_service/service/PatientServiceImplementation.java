package com.medica.medicamanagement.patient_service.service;

import com.medica.dto.AppointmentRequest;
import com.medica.dto.NotificationResponse;
import com.medica.exception.BadRequestException;
import com.medica.medicamanagement.patient_service.dao.PatientRepo;
import com.medica.medicamanagement.patient_service.dto.PatientRequest;
import com.medica.medicamanagement.patient_service.dto.PatientResponse;
import com.medica.medicamanagement.patient_service.models.Patient;
import com.medica.model.AppointmentStatus;
import com.medica.util.BasicUtility;
import com.medica.util.Constant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class PatientServiceImplementation implements PatientService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final PatientRepo patientRepo;
    private static final String TOPIC = "appointment_request_by_patient";
    private final SMSService smsService;

    @Override
    public PatientResponse createPatient(PatientRequest patientRequest) {
        Patient patient = Patient.builder()
                .firstName(patientRequest.getFirstName()).lastName(patientRequest.getLastName()).phone(patientRequest.getPhone())
                .emailId(patientRequest.getEmailId()).address(patientRequest.getAddress()).dob(patientRequest.getDob())
                .medicalHistory(patientRequest.getMedicalHistory())
                .build();

        this.patientRepo.save(patient);
        log.debug(Constant.PATIENT_NOT_FOUND + "{}", patient.getId());

        return PatientResponse.builder().id(patient.getId())
                .firstName(patient.getFirstName()).lastName(patient.getLastName()).phone(patient.getPhone())
                .emailId(patient.getEmailId()).address(patient.getAddress()).dob(patient.getDob())
                .medicalHistory(patient.getMedicalHistory())
                .build();
    }

    @Override
    public void deletePatient(UUID patientId) {
        Patient patient = patientRepo.findById(patientId).orElseThrow(
                () -> new NoSuchElementException(Constant.PATIENT_NOT_FOUND + patientId)
        );
        this.patientRepo.delete(patient);
    }

    @Override
    public PatientResponse updatePatient(PatientRequest patientRequest, UUID patientId) {
        Patient patient = patientRepo.findById(patientId).orElseThrow(
                () -> new NoSuchElementException(Constant.PATIENT_NOT_FOUND + patientId)
        );

        patient.setAddress(patientRequest.getAddress());
        patient.setDob(patientRequest.getDob());
        patient.setPhone(patientRequest.getPhone());
        patient.setFirstName(patientRequest.getFirstName());
        patient.setLastName(patientRequest.getLastName());
        patient.setEmailId(patientRequest.getEmailId());
        patient.setMedicalHistory(patientRequest.getMedicalHistory());

        return PatientResponse.builder().id(patientId)
                .firstName(patient.getFirstName()).lastName(patient.getLastName()).emailId(patient.getEmailId())
                .phone(patient.getPhone()).dob(patient.getDob()).address(patient.getAddress())
                .medicalHistory(patient.getMedicalHistory())
                .build();
    }

    @Override
    public Patient getPatientById(UUID patientId) {
        return patientRepo.findById(patientId).orElseThrow(
                () -> new NoSuchElementException(Constant.PATIENT_NOT_FOUND + patientId)
        );
    }

    @Override
    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    @Override
    public NotificationResponse requestForAppointment(AppointmentRequest request) {
        if (!AppointmentStatus.INITIATED.name().equals(request.getStatus())) {
            throw new BadRequestException("Status must be INITIATED");
        }

        UUID patientId = request.getPatientId();
        Patient patient = this.patientRepo.findById(patientId).orElse(null);
        if (Objects.isNull(patient)) {
            throw new BadRequestException("No Patient Found With Given Id: " + patientId);
        }

        kafkaTemplate.send(TOPIC, BasicUtility.stringifyObject(request));
        return BasicUtility.generateNotificationResponse("Appointment Request Successfully Sent", HttpStatus.OK.name());
    }

    @KafkaListener(topics = "sms-set-by-appointment-setters", groupId = "patient-service-group")
    public void receiveAppointmentStatusViaSMS(String appointmentStatusResponse) {
        String patientId = BasicUtility.readSpecificProperty(appointmentStatusResponse, "patientId");
        String message = BasicUtility.readSpecificProperty(appointmentStatusResponse, "message");

        Patient patient = patientRepo.findById(UUID.fromString(patientId)).orElseThrow(
                () -> new NoSuchElementException("Patient doesn't exist by id: " + patientId)
        );

        smsService.sendSms(patient.getPhone(), message);
    }

    @Override
    public NotificationResponse cancelAppointment(String appointmentId) {
        kafkaTemplate.send("appointment-cancelled-by-patient", appointmentId);
        return BasicUtility.generateNotificationResponse("Appointment Cancelled Successfully By Patient", HttpStatus.OK.name());
    }
}

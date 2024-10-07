package com.medica.medicamanagement.doctor_service.service;

import com.medica.dto.DoctorResponse;
import com.medica.medicamanagement.doctor_service.dao.DoctorRepository;
import com.medica.medicamanagement.doctor_service.dao.SpecializationRepository;
import com.medica.medicamanagement.doctor_service.dto.DoctorRequest;
import com.medica.medicamanagement.doctor_service.model.Doctor;
import com.medica.medicamanagement.doctor_service.model.DoctorAvailability;
import com.medica.medicamanagement.doctor_service.model.Specialization;
import com.medica.medicamanagement.doctor_service.utils.DefaultValuesPopulator;
import com.medica.medicamanagement.doctor_service.utils.ResponseMakerUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImplementation implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;

    @Override
    public List<DoctorResponse> getAllDoctors() {
        List<Doctor> doctors =  doctorRepository.findAll();
        return doctors.stream().map(ResponseMakerUtility::getDoctorResponse).toList();
    }

    @Override
    public DoctorResponse getDoctorById(UUID id) {
        Doctor doctor = this.doctorRepository.findById(id).orElse(null);
        return Objects.isNull(doctor)? null : ResponseMakerUtility.getDoctorResponse(doctor);
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
        return ResponseMakerUtility.getDoctorResponse(doctor);
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
        return ResponseMakerUtility.getDoctorResponse(doctor);
    }
}

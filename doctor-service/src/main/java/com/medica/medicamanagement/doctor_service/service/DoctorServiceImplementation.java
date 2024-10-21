package com.medica.medicamanagement.doctor_service.service;

import com.medica.dto.DoctorResponse;
import com.medica.dto.UserRequest;
import com.medica.dto.UserResponse;
import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.doctor_service.client.UserServiceClient;
import com.medica.medicamanagement.doctor_service.dao.DoctorRepository;
import com.medica.medicamanagement.doctor_service.dao.SpecializationRepository;
import com.medica.medicamanagement.doctor_service.dto.DoctorAvailabilityRepo;
import com.medica.medicamanagement.doctor_service.dto.DoctorRequest;
import com.medica.medicamanagement.doctor_service.model.Doctor;
import com.medica.medicamanagement.doctor_service.model.DoctorAvailability;
import com.medica.medicamanagement.doctor_service.model.Specialization;
import com.medica.medicamanagement.doctor_service.utils.RepositoryUtility;
import com.medica.medicamanagement.doctor_service.utils.RequestMakerUtility;
import com.medica.medicamanagement.doctor_service.utils.ResponseMakerUtility;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImplementation implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;
    private final UserServiceClient userServiceClient;
    private final DoctorAvailabilityRepo doctorAvailabilityRepo;

    @Override
    public Mono<List<DoctorResponse>> getAllDoctors() {
        return Mono.defer(() -> {
            List<Doctor> doctors = doctorRepository.findAll();

            return Flux.fromIterable(doctors)
                    .flatMap(doctor -> this.userServiceClient.getUser(doctor.getUserId().toString())
                            .map(userResponse -> ResponseMakerUtility.getDoctorResponse(doctor, userResponse)))
                    .collectList();
        });
    }

    @Override
    public Mono<DoctorResponse> getDoctorById(UUID id) {
        return RepositoryUtility.getDoctorById(id, doctorRepository)
                .flatMap(doctor ->
                        this.userServiceClient.getUser(doctor.getUserId().toString())
                                .map(userResponse -> ResponseMakerUtility.getDoctorResponse(doctor, userResponse))
                );
    }

    @Override
    @Transactional
    public Mono<Map<String, Object>> createDoctor(DoctorRequest request) {
        Specialization specialization = Specialization.builder()
                .name(request.getSpecializationRequest().getName()).description(request.getSpecializationRequest().getDescription())
                .build();

        UserRequest userRequest = RequestMakerUtility.makeUserRequest(request);

        return this.userServiceClient.createUser(userRequest)
                .flatMap(userResponse -> {
                    Doctor doctor = Doctor.builder()
                            .fee(request.getFee()).userId(userResponse.getId()).specialization(specialization)
                            .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                            .build();

                    // Check if the specialization already exists
                    Specialization existingSpecialization = specializationRepository.findByName(specialization.getName());
                    if (existingSpecialization != null) {
                        doctor.setSpecialization(existingSpecialization);
                    } else {
                        Specialization savedSpecialization = specializationRepository.save(specialization);
                        doctor.setSpecialization(savedSpecialization);
                    }

                    return Mono.fromCallable(() -> doctorRepository.save(doctor))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(savedDoctor -> {
                                log.debug("Doctor saved with ID: {}", savedDoctor.getId());

                                List<DoctorAvailability> doctorAvailabilities = request.getAvailabilities().stream()
                                        .map(availabilityRequest -> DoctorAvailability.builder()
                                                .dayOfWeek(availabilityRequest.getDayOfWeek()).startTime(availabilityRequest.getStartTime())
                                                .endTime(availabilityRequest.getEndTime()).doctor(savedDoctor)
                                                .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                                                .build())
                                        .toList();

                                savedDoctor.setAvailabilities(doctorAvailabilities);
                                return Mono.fromCallable(() -> doctorRepository.save(savedDoctor))
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .map(finalSavedDoctor -> {
                                            log.debug("Doctor and availabilities saved with ID: {}", finalSavedDoctor.getId());

                                            Map<String, Object> response = new HashMap<>();
                                            response.put("doctor", ResponseMakerUtility.getDoctorResponse(finalSavedDoctor, userResponse));
                                            response.put("temporaryPassword", Collections.singletonMap("temporaryPasswordForDoctor", userResponse.getPassword()));

                                            return response;
                                        });
                            });
                })
                .doOnError(e -> log.error("Doctor creation failed: {}", e.getMessage()));
    }

    @Override
    public Mono<DoctorResponse> updateDoctor(UUID id, DoctorRequest request) {
        return Mono.defer(() ->
                RepositoryUtility.getDoctorById(id, doctorRepository).flatMap(doctor -> {
                    UserRequest userRequest = RequestMakerUtility.makeUserRequest(request);

                    return this.userServiceClient.updateUser(doctor.getUserId().toString(), userRequest)
                            .flatMap(userResponse -> Mono.fromCallable(() -> this.specializationRepository.findByName(request.getSpecializationRequest().getName()))
                                    .subscribeOn(Schedulers.boundedElastic())
                                    .flatMap(existingSpecialization -> {
                                        Specialization specialization = (existingSpecialization != null) ? existingSpecialization
                                                : Specialization.builder()
                                                .name(request.getSpecializationRequest().getName())
                                                .description(request.getSpecializationRequest().getDescription())
                                                .build();

                                        doctor.setSpecialization(specialization);
                                        if (!StringUtils.isEmpty(request.getFee())) {
                                            doctor.setFee(request.getFee());
                                        }
                                        doctor.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

                                        // Save the updated doctor
                                        return Mono.fromCallable(() -> this.doctorRepository.save(doctor))
                                                .subscribeOn(Schedulers.boundedElastic())
                                                .map(savedDoctor -> {
                                                    log.debug("Doctor updated with ID: {}", savedDoctor.getId());
                                                    return ResponseMakerUtility.getDoctorResponse(savedDoctor, userResponse);
                                                });
                                    }))
                            .onErrorResume(error -> {
                                log.error("Error updating doctor: {}", error.getMessage());
                                return Mono.error(new InternalServerErrorException("Error updating doctor"));
                            });
                })
        );
    }


    @Override
    public Mono<Void> deleteDoctor(UUID doctorId) {
        return Mono.defer(() ->
                RepositoryUtility.getDoctorById(doctorId, doctorRepository)
                        .flatMap(doctor ->
                                Mono.fromCallable(() -> {
                                            this.userServiceClient.deleteUser(doctor.getUserId().toString())
                                                    .doOnSuccess(aVoid -> {
                                                        log.info("Successfully deleted user for patient ID {}", doctorId);
                                                    })
                                                    .doOnError(error -> {
                                                        log.error("Error occurred while deleting user for patient ID {}: {}", doctorId, error.getMessage());
                                                    }).subscribe();
                                            doctorRepository.delete(doctor);
                                            return null;
                                        })
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .then()
                        )
                        .doOnError(error -> {
                            log.error("Error occurred while retrieving patient ID {}: {}", doctorId, error.getMessage());
                        })
        );
    }
}

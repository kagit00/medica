package com.medica.medicamanagement.doctor_service.service;

import com.medica.dto.DoctorResponse;
import com.medica.dto.UserRequest;
import com.medica.medicamanagement.doctor_service.client.UserServiceClient;
import com.medica.medicamanagement.doctor_service.dao.DoctorRepository;
import com.medica.medicamanagement.doctor_service.dao.SpecializationRepository;
import com.medica.medicamanagement.doctor_service.dto.DoctorRequest;
import com.medica.medicamanagement.doctor_service.model.Doctor;
import com.medica.medicamanagement.doctor_service.model.DoctorAvailability;
import com.medica.medicamanagement.doctor_service.model.Specialization;
import com.medica.medicamanagement.doctor_service.utils.DefaultValuesPopulator;
import com.medica.medicamanagement.doctor_service.utils.RepositoryUtility;
import com.medica.medicamanagement.doctor_service.utils.RequestMakerUtility;
import com.medica.medicamanagement.doctor_service.utils.ResponseMakerUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
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
    public Mono<DoctorResponse> createDoctor(DoctorRequest request) {
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

                    List<DoctorAvailability> doctorAvailabilities = request.getAvailabilities().stream()
                            .map(availabilityRequest -> DoctorAvailability.builder()
                                    .dayOfWeek(availabilityRequest.getDayOfWeek()).startTime(availabilityRequest.getStartTime())
                                    .endTime(availabilityRequest.getEndTime()).createdAt(DefaultValuesPopulator.getCurrentTimestamp())
                                    .updatedAt(DefaultValuesPopulator.getCurrentTimestamp()).doctor(doctor)
                                    .build())
                            .toList();

                    doctor.setAvailabilities(doctorAvailabilities);
                    return Mono.fromCallable(() -> this.specializationRepository.save(specialization)).subscribeOn(Schedulers.boundedElastic())
                            .flatMap(savedSpecialization -> {
                                doctor.setSpecialization(savedSpecialization);
                                return Mono.fromCallable(() -> this.doctorRepository.save(doctor)).subscribeOn(Schedulers.boundedElastic())
                                        .map(savedDoctor -> {
                                            log.debug("Doctor saved with ID: {}", savedDoctor.getId());
                                            return ResponseMakerUtility.getDoctorResponse(savedDoctor, userResponse);
                                        });
                            });
                });
    }

    @Override
    public Mono<DoctorResponse> updateDoctor(UUID id, DoctorRequest request) {
        return Mono.defer(() ->
                RepositoryUtility.getDoctorById(id, doctorRepository).flatMap(doctor -> {
                            Specialization specialization = Specialization.builder()
                                    .name(request.getSpecializationRequest().getName())
                                    .description(request.getSpecializationRequest().getDescription())
                                    .build();

                            UserRequest userRequest = RequestMakerUtility.makeUserRequest(request);

                            return this.userServiceClient.updateUser(doctor.getUserId().toString(), userRequest)
                                    .flatMap(userResponse -> {

                                        doctor.setSpecialization(specialization);
                                        if (!StringUtils.isEmpty(request.getFee())) {
                                            doctor.setFee(request.getFee());
                                        }
                                        doctor.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

                                        return Mono.fromCallable(() -> this.doctorRepository.save(doctor))
                                                .subscribeOn(Schedulers.boundedElastic())
                                                .map(savedDoctor -> {
                                                    log.debug("Doctor updated with ID: {}", savedDoctor.getId());
                                                    return ResponseMakerUtility.getDoctorResponse(savedDoctor, userResponse);
                                                });
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

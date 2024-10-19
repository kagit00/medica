package com.medica.medicamanagement.patient_service.service;

import com.medica.dto.PatientRequest;
import com.medica.dto.PatientResponse;
import com.medica.dto.UserRequest;
import com.medica.medicamanagement.patient_service.client.UserServiceClient;
import com.medica.medicamanagement.patient_service.dao.PatientRepo;
import com.medica.medicamanagement.patient_service.models.Patient;
import com.medica.medicamanagement.patient_service.util.RepositoryUtility;
import com.medica.medicamanagement.patient_service.util.RequestMakerUtility;
import com.medica.medicamanagement.patient_service.util.ResponseMakerUtility;
import com.medica.util.Constant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class PatientServiceImplementation implements PatientService {
    private final PatientRepo patientRepo;
    private final UserServiceClient userServiceClient;

    @Override
    public Mono<Map<String, Object>> createPatient(PatientRequest patientRequest) {
        UserRequest userRequest = RequestMakerUtility.makeUserCreationRequest(patientRequest);

        return this.userServiceClient.createUser(userRequest)
                .flatMap(userResponse -> {
                    Patient patient = Patient.builder().userId(userResponse.getId()).medicalHistory(patientRequest.getMedicalHistory())
                            .build();

                    return Mono.fromCallable(() -> this.patientRepo.save(patient))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(savedPatient -> {
                                log.debug(Constant.PATIENT_NOT_FOUND + "{}", savedPatient.getId());

                                Map<String, Object> response = new HashMap<>();
                                response.put("patient", ResponseMakerUtility.getPatientResponse(patient, userResponse));
                                response.put(
                                        "temporaryPassword",
                                        Collections.singletonMap("temporaryPasswordForPatient", userResponse.getPassword())
                                );

                                return response;
                            });
                });
    }

    @Override
    public Mono<Void> deletePatient(UUID patientId) {
        return Mono.defer(() ->
                RepositoryUtility.getPatientById(patientId, patientRepo)
                        .flatMap(patient ->
                                Mono.fromCallable(() -> {
                                            this.userServiceClient.deleteUser(patient.getUserId().toString())
                                                    .doOnSuccess(aVoid -> {
                                                        log.info("Successfully deleted user for patient ID {}", patientId);
                                                    })
                                                    .doOnError(error -> {
                                                        log.error("Error occurred while deleting user for patient ID {}: {}", patientId, error.getMessage());
                                                    }).subscribe();
                                            patientRepo.delete(patient);
                                            return null;
                                        })
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .then()
                        )
                        .doOnError(error -> {
                            log.error("Error occurred while retrieving patient ID {}: {}", patientId, error.getMessage());
                        })
        );
    }

    @Override
    public Mono<PatientResponse> updatePatient(PatientRequest patientRequest, UUID patientId) {
        return Mono.defer(() ->
                RepositoryUtility.getPatientById(patientId, patientRepo)
                        .flatMap(patient -> {
                            UserRequest userRequest = RequestMakerUtility.makeUserUpdateRequest(patientRequest);
                            return this.userServiceClient.updateUser(patient.getUserId().toString(), userRequest)
                                    .doOnNext(userResponse -> {
                                        patient.setMedicalHistory(patientRequest.getMedicalHistory());
                                    })
                                    .map(userResponse -> ResponseMakerUtility.getPatientResponse(patient, userResponse));
                        })
        );
    }

    @Override
    public Mono<PatientResponse> getPatientById(UUID patientId) {
        return RepositoryUtility.getPatientById(patientId, patientRepo)
                .flatMap(patient ->
                        this.userServiceClient.getUser(patient.getUserId().toString())
                                .map(userResponse -> ResponseMakerUtility.getPatientResponse(patient, userResponse))
                );
    }

    @Override
    public Mono<List<PatientResponse>> getAllPatients() {
        return Mono.defer(() -> {
            List<Patient> patients = patientRepo.findAll();

            return Flux.fromIterable(patients)
                    .flatMap(patient -> this.userServiceClient.getUser(patient.getUserId().toString())
                            .map(userResponse -> ResponseMakerUtility.getPatientResponse(patient, userResponse)))
                    .collectList();
        });
    }

}

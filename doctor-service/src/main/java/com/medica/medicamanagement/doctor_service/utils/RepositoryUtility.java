package com.medica.medicamanagement.doctor_service.utils;

import com.medica.exception.BadRequestException;
import com.medica.medicamanagement.doctor_service.dao.DoctorRepository;
import com.medica.medicamanagement.doctor_service.dao.SpecializationRepository;
import com.medica.medicamanagement.doctor_service.model.Doctor;
import com.medica.medicamanagement.doctor_service.model.Specialization;
import com.medica.util.Constant;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.NoSuchElementException;
import java.util.UUID;

public final class RepositoryUtility {

    private RepositoryUtility() {
        throw new UnsupportedOperationException("unsupported Operation");
    }

    public static Mono<Doctor> getDoctorById(UUID doctorId, DoctorRepository docRepo) {
        return Mono.fromCallable(() -> docRepo.findById(doctorId)
                .orElseThrow(() -> new BadRequestException("Doctor not found")));
    }

    public static Mono<Specialization> findSpecializationByName(String name, SpecializationRepository specializationRepository) {
        return Mono.fromCallable(() -> specializationRepository.findByName(name))
                .subscribeOn(Schedulers.boundedElastic());
    }
}

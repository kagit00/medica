package com.medica.medicamanagement.patient_service.util;

import com.medica.dto.PatientRequest;
import com.medica.dto.RoleReq;
import com.medica.dto.UserRequest;

import java.util.List;
import java.util.Set;

/**
 * The type Request maker utility.
 */
public final class RequestMakerUtility {

    private RequestMakerUtility() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Make user creation request user request.
     *
     * @param patientRequest the patient request
     * @return the user request
     */
    public static UserRequest makeUserCreationRequest(PatientRequest patientRequest) {
        return UserRequest.builder()
                .phone(patientRequest.getPhone()).address(patientRequest.getAddress()).email(patientRequest.getEmailId())
                .lastName(patientRequest.getLastName()).firstName(patientRequest.getFirstName())
                .age(patientRequest.getAge()).username(patientRequest.getUsername())
                .roles(List.of(RoleReq.builder().name("PATIENT").build()))
                .build();
    }

    /**
     * Make user update request user request.
     *
     * @param patientRequest the patient request
     * @return the user request
     */
    public static UserRequest makeUserUpdateRequest(PatientRequest patientRequest) {
        return UserRequest.builder()
                .phone(patientRequest.getPhone()).address(patientRequest.getAddress()).email(patientRequest.getEmailId())
                .lastName(patientRequest.getLastName()).firstName(patientRequest.getFirstName())
                .age(patientRequest.getAge()).username(patientRequest.getUsername())
                .roles(List.of(RoleReq.builder().name("PATIENT").build()))
                .build();
    }
}

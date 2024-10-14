package com.medica.medicamanagement.doctor_service.utils;

import com.medica.dto.RoleReq;
import com.medica.dto.UserRequest;
import com.medica.medicamanagement.doctor_service.dto.DoctorRequest;

import java.util.List;
import java.util.Set;

/**
 * The type Request maker utility.
 */
public class RequestMakerUtility {

    private RequestMakerUtility() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Make user request user request.
     *
     * @param doctorRequest the doctor request
     * @return the user request
     */
    public static UserRequest makeUserRequest(DoctorRequest doctorRequest) {
        return UserRequest.builder()
                .phone(doctorRequest.getPhone()).address(doctorRequest.getAddress())
                .lastName(doctorRequest.getLastName()).firstName(doctorRequest.getFirstName())
                .age(doctorRequest.getAge()).username(doctorRequest.getUsername())
                .roles(List.of(RoleReq.builder().name("DOCTOR").build()))
                .build();
    }
}

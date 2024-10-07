package com.medica.medicamanagement.doctor_service.utils;

import com.medica.dto.DoctorApprovalResponse;
import com.medica.dto.DoctorAvailabilityResponse;
import com.medica.dto.DoctorResponse;
import com.medica.dto.SpecializationResponse;
import com.medica.medicamanagement.doctor_service.model.Doctor;
import com.medica.medicamanagement.doctor_service.model.DoctorApproval;

import java.util.List;

public final class ResponseMakerUtility {

    private ResponseMakerUtility() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    public static DoctorResponse getDoctorResponse(Doctor doctor) {
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

    public static DoctorApprovalResponse getDoctorApprovalResponse(DoctorApproval doctorApproval) {
        return DoctorApprovalResponse.builder()
                .appointmentId(doctorApproval.getAppointmentId()).doctorComments(doctorApproval.getDoctorComments())
                .doctorId(doctorApproval.getDoctorId()).status(doctorApproval.getStatus())
                .build();
    }
}

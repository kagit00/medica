package com.medica.medicamanagement.doctor_service.client;

import com.medica.dto.DoctorApprovalResponse;
import reactor.core.publisher.Mono;

public interface AppointmentServiceClient {
    Mono<DoctorApprovalResponse> approveAppointmentRequest(DoctorApprovalResponse response);
}

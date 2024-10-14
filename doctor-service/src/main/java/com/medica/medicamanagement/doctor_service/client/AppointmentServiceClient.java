package com.medica.medicamanagement.doctor_service.client;

import com.medica.dto.DoctorApprovalResponse;
import reactor.core.publisher.Mono;

/**
 * The interface Appointment service client.
 */
public interface AppointmentServiceClient {
    /**
     * Approve appointment request mono.
     *
     * @param response the response
     * @return the mono
     */
    Mono<DoctorApprovalResponse> approveAppointmentRequest(DoctorApprovalResponse response);
}

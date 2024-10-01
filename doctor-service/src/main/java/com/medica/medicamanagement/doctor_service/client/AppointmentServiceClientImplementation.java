package com.medica.medicamanagement.doctor_service.client;

import com.medica.dto.DoctorApprovalResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AppointmentServiceClientImplementation implements AppointmentServiceClient {
    private final WebClient webClient;

    public AppointmentServiceClientImplementation(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8081").build();
    }

    @Override
    public Mono<DoctorApprovalResponse> approveAppointmentRequest(DoctorApprovalResponse response) {
        return this.webClient.post().uri("/api/appointments/doctor-approval").retrieve().bodyToMono(DoctorApprovalResponse.class);
    }
}

package com.medica.medicamanagement.doctor_service.client;

import com.medica.dto.AppointmentResponse;
import com.medica.dto.DoctorApprovalResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AppointmentServiceClientImplementation implements AppointmentServiceClient {
    private final WebClient webClient;

    @Value("${appointments.server.domain}")
    private String appointmentServer;

    public AppointmentServiceClientImplementation(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<DoctorApprovalResponse> approveAppointmentRequest(DoctorApprovalResponse response) {
        return this.webClient.post().uri(appointmentServer + "/api/appointments/doctor-approval").retrieve().bodyToMono(DoctorApprovalResponse.class);
    }
}

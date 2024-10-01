package com.medica.medicamanagement.appointment_service.client;

import com.medica.dto.NotificationResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DoctorServiceClientImplementation implements DoctorServiceClient {
    private final WebClient webClient;

    public DoctorServiceClientImplementation(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    @Override
    public void getDoctorById(String id) {
        webClient.get().uri("/api/doctors/{id}", id)
                .retrieve().bodyToMono(NotificationResponse.class)
                .block();
    }
}

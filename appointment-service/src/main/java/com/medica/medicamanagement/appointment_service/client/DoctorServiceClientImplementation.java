package com.medica.medicamanagement.appointment_service.client;

import com.medica.dto.DoctorResponse;
import com.medica.exception.InternalServerErrorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class DoctorServiceClientImplementation implements DoctorServiceClient {
    private final WebClient webClient;

    @Value("${doctors.server.domain}")
    private String doctorsServerDomain;

    public DoctorServiceClientImplementation(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public DoctorResponse getDoctorById(String id) {
        if (doctorsServerDomain == null || doctorsServerDomain.isEmpty()) {
            throw new InternalServerErrorException("doctorsServerDomain property must not be null or empty");
        }

        return webClient.get()
                .uri(doctorsServerDomain + "/api/doctors/doctor/{id}", id)
                .retrieve()
                .bodyToMono(DoctorResponse.class)
                .block();
    }
}


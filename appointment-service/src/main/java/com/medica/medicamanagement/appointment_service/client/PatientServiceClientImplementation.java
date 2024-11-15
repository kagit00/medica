package com.medica.medicamanagement.appointment_service.client;

import com.medica.dto.PatientResponse;
import com.medica.exception.BadRequestException;
import com.medica.exception.InternalServerErrorException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PatientServiceClientImplementation implements PatientServiceClient {
    private final WebClient webClient;

    @Value("${patients.server.domain}")
    private String patientsServerDomain;

    public PatientServiceClientImplementation(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public PatientResponse getPatientById(String patientId) {
        if (StringUtils.isEmpty(patientsServerDomain)) {
            throw new BadRequestException("patientsServerDomain property must not be null or empty");
        }

        return webClient.get().uri(patientsServerDomain + "/api/patients/patient/{patientId}", patientId)
                .retrieve().bodyToMono(PatientResponse.class)
                .block();
    }
}

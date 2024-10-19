package com.medica.medicamanagement.user_service.client;

import com.medica.dto.KeyCloakTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;


@Service
@Slf4j
public class KeyCloakTokenServiceClientImpl implements KeyCloakTokenServiceClient {

    private final WebClient keycloakWebClient;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Value("${keycloak.client-id}")
    private String keycloakClientId;

    @Value("${keycloak.client-secret}")
    private String keycloakClientSecret;

    @Autowired
    public KeyCloakTokenServiceClientImpl(WebClient keycloakWebClient) {
        this.keycloakWebClient = keycloakWebClient;
    }

    @Override
    public Mono<String> getAdminAccessToken() {
        return this.keycloakWebClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", keycloakRealm)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials")
                        .with("client_id", keycloakClientId)
                        .with("client_secret", keycloakClientSecret))
                .retrieve()
                .bodyToMono(KeyCloakTokenResponse.class)
                .map(KeyCloakTokenResponse::getAccessToken)
                .doOnNext(accessToken -> log.debug("Access Token: {}", accessToken))
                .doOnError(error -> log.error("Error while fetching token: {}", error.getMessage()));
    }
}

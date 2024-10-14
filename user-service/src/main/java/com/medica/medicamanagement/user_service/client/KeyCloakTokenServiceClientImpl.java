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

/**
 * The `KeyCloakTokenServiceClientImpl` class in Java is a service implementation that retrieves an
 * admin access token from Keycloak using WebClient.
 */
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

    /**
     * This function sends a POST request to a Keycloak server to obtain an admin access token using
     * client credentials.
     * 
     * @return A `Mono<String>` is being returned. This method fetches an admin access token from
     * Keycloak using client credentials grant type and returns the access token as a `Mono<String>`.
     */
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

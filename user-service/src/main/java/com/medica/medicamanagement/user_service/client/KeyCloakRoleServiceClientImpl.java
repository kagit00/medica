package com.medica.medicamanagement.user_service.client;

import com.medica.dto.KeycloakRole;
import com.medica.exception.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

/**
 * The `KeyCloakRoleServiceClientImpl` class in Java is a service implementation that interacts with
 * Keycloak to manage roles for users.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KeyCloakRoleServiceClientImpl implements KeyCloakRoleServiceClient {
    private final WebClient keycloakWebClient;
    private final KeyCloakTokenServiceClient keyCloakTokenServiceClient;


    @Value("${keycloak.realm}")
    private String keycloakRealm;


    @Override
    public Mono<KeycloakRole> getRoleByName(String roleName, String accessToken) {
        return keycloakWebClient.get()
                .uri("/admin/realms/{realm}/roles/{roleName}", keycloakRealm, roleName)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KeycloakRole.class)
                .doOnSuccess(role -> log.info("Successfully retrieved KeycloakRole: {}", roleName))
                .doOnError(error -> log.error("Failed to retrieve KeycloakRole for role {}: {}", roleName, error.getMessage()));
    }

    @Override
    public Mono<Void> assignRolesToUserInKeycloak(String userId, List<KeycloakRole> roles, String accessToken) {
        return keycloakWebClient.post()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", keycloakRealm, userId)
                .header("Authorization", "Bearer " + accessToken)
                .body(Mono.just(roles), List.class)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> log.error("Failed to assign roles to user {}: {}", userId, error.getMessage()));
    }

    @Override
    public Flux<KeycloakRole> getRolesForUserInKeycloak(String userId, String accessToken) {
        return keycloakWebClient.get()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", keycloakRealm, userId)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("400 Client Error: {} - {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new InternalServerErrorException("Client error: " + errorBody));
                                }))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("500 Server Error: {} - {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new InternalServerErrorException("Server error: " + errorBody));
                                }))
                .bodyToFlux(KeycloakRole.class)
                .doOnError(error -> log.error("Error retrieving roles for user {}: {}", userId, error.getMessage()));
    }


    @Override
    public Mono<Void> removeRolesFromUserInKeycloak(String userId, List<KeycloakRole> roles, String accessToken) {
        return Flux.fromIterable(roles)
                .flatMap(role -> keycloakWebClient.delete()
                        .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm/{roleName}", keycloakRealm, userId, role.getName())
                        .header("Authorization", "Bearer " + accessToken)
                        .retrieve()
                        .bodyToMono(Void.class))
                .then();
    }


}

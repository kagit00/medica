package com.medica.medicamanagement.user_service.client;

import com.medica.dto.KeycloakUserRequest;
import com.medica.dto.UserRequest;
import com.medica.exception.InternalServerErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;


@Service
@RequiredArgsConstructor
@Slf4j
public class KeyCloakUserServiceClientImpl implements KeyCloakUserServiceClient {
    private final WebClient keycloakWebClient;
    private final KeyCloakTokenServiceClient keyCloakTokenServiceClient;

    @Value("${keycloak.realm}")
    private String keycloakRealm;


    @Override
    public Mono<String> createUserInKeycloak(UserRequest userRequest) {
        KeycloakUserRequest keycloakUserRequest = KeycloakUserRequest.builder()
                .username(userRequest.getUsername()).email(userRequest.getEmail()).firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName()).enabled(true)
                .build();

        return keyCloakTokenServiceClient.getAdminAccessToken().flatMap(accessToken -> this.keycloakWebClient.post()
                .uri("/admin/realms/{realm}/users", keycloakRealm)
                .header("Authorization", "Bearer " + accessToken)
                .body(Mono.just(keycloakUserRequest), KeycloakUserRequest.class).exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(response.headers().header("Location").get(0).substring(
                                response.headers().header("Location").get(0).lastIndexOf("/") + 1)
                        );
                    } else {
                        return response.bodyToMono(String.class).flatMap(errorMessage ->
                                        Mono.error(new InternalServerErrorException("Failed to create user: " + errorMessage))
                        );
                    }
                }));
    }

    @Override
    public Mono<String> updateUserInKeycloak(String userId, UserRequest userRequest, String accessToken) {
        KeycloakUserRequest keycloakUserRequest = KeycloakUserRequest.builder()
                .username(userRequest.getUsername()).email(userRequest.getEmail()).firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName()).enabled(true)
                .build();

        return this.keycloakWebClient.put()
                .uri("/admin/realms/{realm}/users/{id}", keycloakRealm, userId)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(keycloakUserRequest), KeycloakUserRequest.class)
                .exchangeToMono(response -> {
                    log.debug("Received response with status code: {}", response.statusCode());
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(userId);
                    } else {
                        return response.bodyToMono(String.class)
                                .doOnNext(errorMessage -> log.error("Failed to update user: {}", errorMessage))
                                .flatMap(errorMessage -> Mono.error(new InternalServerErrorException("Failed to update user: " + errorMessage)));
                    }
                })
                .doOnError(error -> log.error("Error updating user: {}", error.getMessage()));
    }


    @Override
    public Mono<Void> deleteUserInKeycloak(String userId) {
        return keyCloakTokenServiceClient.getAdminAccessToken().flatMap(accessToken ->
                this.keycloakWebClient.delete()
                        .uri("/admin/realms/{realm}/users/{userId}", keycloakRealm, userId)
                        .header("Authorization", "Bearer " + accessToken)
                        .exchangeToMono(response -> {
                            if (response.statusCode().is2xxSuccessful()) {
                                return Mono.empty();
                            } else {
                                return response.bodyToMono(String.class).flatMap(errorMessage ->
                                        Mono.error(new InternalServerErrorException("Failed to delete user: " + errorMessage))
                                );
                            }
                        })
        );
    }

}

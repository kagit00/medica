package com.medica.medicamanagement.user_service.client;

import com.medica.dto.KeycloakUserRequest;
import com.medica.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The `KeyCloakUserServiceClientImpl` class in Java is a service implementation that interacts with
 * Keycloak to create, update, and delete users using WebClient and KeyCloakTokenServiceClient.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeyCloakUserServiceClientImpl implements KeyCloakUserServiceClient {
    private final WebClient keycloakWebClient;
    private final KeyCloakTokenServiceClient keyCloakTokenServiceClient;

    @Value("${keycloak.realm}")
    private String keycloakRealm;


    /**
     * The `createUserInKeycloak` method creates a user in Keycloak using the provided user information
     * and returns a Mono containing the user's ID if successful, or an error message if the creation
     * fails.
     * 
     * @param userRequest The `createUserInKeycloak` method takes a `UserRequest` object as a
     * parameter. This object contains information about the user to be created in Keycloak, such as
     * the username, email, first name, and last name.
     * @return The `createUserInKeycloak` method returns a `Mono<String>`.
     */
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
                                        Mono.error(new RuntimeException("Failed to create user: " + errorMessage))
                        );
                    }
                }));
    }

    /**
     * This function updates a user in Keycloak using a KeycloakUserRequest object and returns a
     * Mono<String> indicating success or failure.
     * 
     * @param userId The `userId` parameter in the `updateUserInKeycloak` method represents the unique
     * identifier of the user in Keycloak that you want to update. This identifier is used to specify
     * the user whose information will be modified based on the `UserRequest` provided.
     * @param userRequest The `userRequest` parameter in the `updateUserInKeycloak` method contains
     * information about the user that needs to be updated in Keycloak. It includes the user's
     * username, email, first name, last name, and whether the user is enabled or not. This information
     * is used to create
     * @param accessToken An access token is a credential used to access protected resources on behalf
     * of a user. It is typically obtained after a user successfully authenticates and authorizes
     * access to their data. In the provided code snippet, the `accessToken` is used to authenticate
     * the request to update a user in Keycloak. It
     * @return The `updateUserInKeycloak` method returns a `Mono<String>`.
     */
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
                                .flatMap(errorMessage -> Mono.error(new RuntimeException("Failed to update user: " + errorMessage)));
                    }
                })
                .doOnError(error -> log.error("Error updating user: {}", error.getMessage()));
    }


    /**
     * This function deletes a user in Keycloak using an admin access token obtained from a Keycloak
     * token service client.
     * 
     * @param userId The `userId` parameter in the `deleteUserInKeycloak` method represents the unique
     * identifier of the user that you want to delete from the Keycloak server. This identifier is
     * typically assigned to each user during user creation and is used to uniquely identify and manage
     * user accounts within the Keycloak realm.
     * @return A `Mono<Void>` is being returned.
     */
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
                                        Mono.error(new RuntimeException("Failed to delete user: " + errorMessage))
                                );
                            }
                        })
        );
    }

}

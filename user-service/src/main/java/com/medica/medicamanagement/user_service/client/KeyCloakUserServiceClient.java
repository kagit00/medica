package com.medica.medicamanagement.user_service.client;

import com.medica.dto.UserRequest;
import reactor.core.publisher.Mono;

/**
 * The interface Key cloak user service client.
 */
public interface KeyCloakUserServiceClient {

    /**
     * Create user in keycloak mono.
     *
     * @param userRequest the user request
     * @return the mono
     */
    Mono<String> createUserInKeycloak(UserRequest userRequest);

    /**
     * Update user in keycloak mono.
     *
     * @param userId      the user id
     * @param userRequest the user request
     * @param accessToken the access token
     * @return the mono
     */
    Mono<String>  updateUserInKeycloak(String userId, UserRequest userRequest, String accessToken);

    /**
     * Delete user in keycloak mono.
     *
     * @param userId the user id
     * @return the mono
     */
    Mono<Void> deleteUserInKeycloak(String userId);
}

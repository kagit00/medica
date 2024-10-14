package com.medica.medicamanagement.doctor_service.client;

import com.medica.dto.UserRequest;
import com.medica.dto.UserResponse;
import reactor.core.publisher.Mono;

/**
 * The interface User service client.
 */
public interface UserServiceClient {
    /**
     * Create user mono.
     *
     * @param userRequest the user request
     * @return the mono
     */
    Mono<UserResponse> createUser(UserRequest userRequest);

    /**
     * Update user mono.
     *
     * @param userId      the user id
     * @param userRequest the user request
     * @return the mono
     */
    Mono<UserResponse> updateUser(String userId, UserRequest userRequest);

    /**
     * Gets user.
     *
     * @param userId the user id
     * @return the user
     */
    Mono<UserResponse> getUser(String userId);

    /**
     * Delete user mono.
     *
     * @param userId the user id
     * @return the mono
     */
    Mono<Void> deleteUser(String userId);
}

package com.medica.medicamanagement.user_service.service;

import com.medica.dto.NotificationResponse;
import com.medica.dto.UserPasswordRequest;
import com.medica.dto.UserRequest;
import com.medica.dto.UserResponse;
import reactor.core.publisher.Mono;

/**
 * The interface User service.
 */
public interface UserService {

    /**
     * Create user mono.
     *
     * @param userRequest the user request
     * @return the mono
     */
    Mono<UserResponse> createUser(UserRequest userRequest);

    /**
     * Gets user by id.
     *
     * @param userId the user id
     * @return the user by id
     */
    Mono<UserResponse> getUserById(String userId);

    /**
     * Update user mono.
     *
     * @param userId      the user id
     * @param userRequest the user request
     * @return the mono
     */
    Mono<UserResponse> updateUser(String userId, UserRequest userRequest);

    /**
     * Delete user mono.
     *
     * @param userId the user id
     * @return the mono
     */
    Mono<Void> deleteUser(String userId);

    Mono<NotificationResponse> changePassword(String username, UserPasswordRequest userPasswordRequest);
}

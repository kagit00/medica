package com.medica.medicamanagement.user_service.service;

import com.medica.dto.UserRequest;
import com.medica.dto.UserResponse;
import reactor.core.publisher.Mono;

// This code snippet is defining a Java interface named `UserService`. This interface declares four
// methods that are used for user management operations in a reactive programming style using Project
// Reactor's `Mono` type:
public interface UserService {
    /**
     * The function `createUser` takes a `UserRequest` object as input and returns a `Mono` of
     * `UserResponse`.
     * 
     * @param userRequest The `userRequest` parameter is an object of type `UserRequest`, which likely
     * contains the information needed to create a new user, such as the user's name, email, and any
     * other relevant details.
     * @return A Mono object containing a UserResponse is being returned.
     */
    Mono<UserResponse> createUser(UserRequest userRequest);
    /**
     * This function retrieves a user's information based on their user ID.
     * 
     * @param userId The `userId` parameter is a unique identifier that is used to retrieve information
     * about a specific user.
     * @return A Mono object containing a UserResponse for the specified userId is being returned.
     */
    Mono<UserResponse> getUserById(String userId);
    /**
     * The function `updateUser` takes a user ID and a user request, and returns a Mono containing a
     * UserResponse.
     * 
     * @param userId The `userId` parameter is a unique identifier for the user whose information is
     * being updated.
     * @param userRequest The `userRequest` parameter is an object of type `UserRequest` that contains
     * the data needed to update a user. It likely includes information such as the user's name, email,
     * and any other details that need to be updated.
     * @return A Mono object containing a UserResponse is being returned.
     */
    Mono<UserResponse> updateUser(String userId, UserRequest userRequest);
    /**
     * The function deleteUser takes a userId as input and deletes the corresponding user, returning a
     * Mono<Void> indicating completion.
     * 
     * @param userId The `userId` parameter is a unique identifier that represents the user to be
     * deleted.
     * @return The method `deleteUser` is returning a `Mono<Void>`. This indicates that the method is
     * returning a reactive type `Mono` which represents a completion signal without a value (`Void`).
     */
    Mono<Void> deleteUser(String userId);
}

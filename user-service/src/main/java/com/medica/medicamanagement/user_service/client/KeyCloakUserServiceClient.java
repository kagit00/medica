package com.medica.medicamanagement.user_service.client;

import com.medica.dto.UserRequest;
import reactor.core.publisher.Mono;

// This Java code snippet is defining an interface named `KeyCloakUserServiceClient`. This interface
// declares three methods that are used for interacting with a Keycloak user service:
public interface KeyCloakUserServiceClient {
    
    
    /**
     * The function `createUserInKeycloak` takes a `UserRequest` object and returns a `Mono<String>`
     * representing the created user in Keycloak.
     * 
     * @param userRequest The `userRequest` parameter is an object of type `UserRequest`, which likely
     * contains information needed to create a user in Keycloak, such as the user's username, password,
     * email, and any other relevant details.
     * @return A `Mono<String>` is being returned.
     */
    Mono<String> createUserInKeycloak(UserRequest userRequest);
    
    
    /**
     * This function updates a user in Keycloak using the provided user ID, user request data, and
     * access token.
     * 
     * @param userId The `userId` parameter is a unique identifier for the user in Keycloak that you
     * want to update.
     * @param userRequest The `userRequest` parameter is an object of type `UserRequest`, which likely
     * contains information about the user that needs to be updated in Keycloak. This object may
     * include fields such as the user's name, email, password, roles, or any other relevant
     * information needed for the update operation.
     * @param accessToken The `accessToken` parameter is a string that represents the access token used
     * for authentication and authorization when making requests to the Keycloak server. This token is
     * typically obtained after a successful authentication process and is used to access protected
     * resources on the server on behalf of the user.
     * @return The method `updateUserInKeycloak` is returning a `Mono<String>`, which indicates that it
     * is returning a reactive type that emits a single `String` value.
     */
    Mono<String>  updateUserInKeycloak(String userId, UserRequest userRequest, String accessToken);
    
    
    /**
     * The function deleteUserInKeycloak deletes a user in Keycloak and returns a Mono<Void>.
     * 
     * @param userId The `userId` parameter is a unique identifier that represents the user to be
     * deleted in Keycloak.
     * @return The method `deleteUserInKeycloak` is returning a `Mono<Void>`. This indicates that it is
     * returning a reactive type `Mono` which represents a completion signal without a value, in this
     * case, indicating the completion of the deletion operation for a user in Keycloak.
     */
    Mono<Void> deleteUserInKeycloak(String userId);
}

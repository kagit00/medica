package com.medica.medicamanagement.user_service.client;

import com.medica.dto.KeycloakRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// This Java interface `KeyCloakRoleServiceClient` defines a contract for interacting with a Keycloak
// Role Service Client. It declares the following methods:
public interface KeyCloakRoleServiceClient {
    /**
     * This function retrieves a Keycloak role by its name using an access token.
     * 
     * @param roleName The roleName parameter is a String that represents the name of the Keycloak role
     * that you want to retrieve.
     * @param accessToken The `accessToken` parameter is a string that represents the access token used
     * for authentication and authorization purposes. It is typically obtained after a successful
     * authentication process and is used to access protected resources or perform actions on behalf of
     * the authenticated user.
     * @return The method `getRoleByName` returns a `Mono` object containing a `KeycloakRole`.
     */
    Mono<KeycloakRole> getRoleByName(String roleName, String accessToken);


    /**
     * This function assigns roles to a user in Keycloak using the provided user ID, roles, and access
     * token.
     * 
     * @param userId The `userId` parameter is a unique identifier for the user in Keycloak to whom you
     * want to assign the specified roles.
     * @param roles The `roles` parameter is a list of `KeycloakRole` objects that represent the roles
     * you want to assign to a user in Keycloak. Each `KeycloakRole` object typically contains
     * information such as the role name, ID, and any other relevant details needed to assign the role
     * to a
     * @param accessToken An access token is a credential used to access protected resources on behalf
     * of a user. In this context, the access token is likely used to authenticate the request to
     * assign roles to a user in Keycloak.
     * @return The method `assignRolesToUserInKeycloak` is returning a `Mono<Void>`, which represents a
     * completion signal without a value. This means that the method is likely performing some
     * asynchronous operation to assign roles to a user in Keycloak and will complete without returning
     * any specific result or value.
     */
    Mono<Void> assignRolesToUserInKeycloak(String userId, List<KeycloakRole> roles, String accessToken);


    /**
     * This function retrieves Keycloak roles for a specific user using their user ID and access token.
     * 
     * @param userId The `userId` parameter is a unique identifier for a user in Keycloak. It is used
     * to specify which user's roles you want to retrieve from Keycloak.
     * @param accessToken An access token is a credential used to access protected resources on behalf
     * of a user. In this context, the access token is likely used to authenticate the user and
     * authorize access to Keycloak resources, such as roles associated with a specific user.
     * @return The method `getRolesForUserInKeycloak` is returning a `Flux` of `KeycloakRole` objects.
     */
    Flux<KeycloakRole> getRolesForUserInKeycloak(String userId, String accessToken);


    /**
     * This function removes a list of roles from a user in Keycloak using the provided user ID and
     * access token.
     * 
     * @param userId The `userId` parameter is a string that represents the unique identifier of a user
     * in Keycloak.
     * @param roles The `roles` parameter is a list of KeycloakRole objects that represent the roles to
     * be removed from a user in Keycloak.
     * @param accessToken An access token is a credential used to access protected resources on behalf
     * of a user. In this context, the access token is likely used to authenticate the request to the
     * Keycloak server in order to remove roles from a user.
     * @return The method `removeRolesFromUserInKeycloak` is returning a `Mono<Void>`. This indicates
     * that it is returning a reactive type `Mono` which represents a completion signal without a
     * value, in this case, indicating the completion of the operation without returning any specific
     * result.
     */
    Mono<Void> removeRolesFromUserInKeycloak(String userId, List<KeycloakRole> roles, String accessToken);
}

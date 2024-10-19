package com.medica.medicamanagement.user_service.client;

import com.medica.dto.KeycloakRole;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


/**
 * The interface Key cloak role service client.
 */
public interface KeyCloakRoleServiceClient {

    /**
     * Gets role by name.
     *
     * @param roleName    the role name
     * @param accessToken the access token
     * @return the role by name
     */
    Mono<KeycloakRole> getRoleByName(String roleName, String accessToken);

    /**
     * Assign roles to user in keycloak mono.
     *
     * @param userId      the user id
     * @param roles       the roles
     * @param accessToken the access token
     * @return the mono
     */
    Mono<Void> assignRolesToUserInKeycloak(String userId, List<KeycloakRole> roles, String accessToken);

    /**
     * Gets roles for user in keycloak.
     *
     * @param userId      the user id
     * @param accessToken the access token
     * @return the roles for user in keycloak
     */
    Flux<KeycloakRole> getRolesForUserInKeycloak(String userId, String accessToken);

    /**
     * Remove roles from user in keycloak mono.
     *
     * @param userId      the user id
     * @param roles       the roles
     * @param accessToken the access token
     * @return the mono
     */
    Mono<Void> removeRolesFromUserInKeycloak(String userId, List<KeycloakRole> roles, String accessToken);
}

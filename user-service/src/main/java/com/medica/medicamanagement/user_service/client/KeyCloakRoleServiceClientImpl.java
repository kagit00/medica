package com.medica.medicamanagement.user_service.client;

import com.medica.dto.KeycloakRole;
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

    /**
     * This function retrieves a Keycloak role by name using a WebClient with authorization and logs
     * success or failure.
     * 
     * @param roleName The `roleName` parameter in the `getRoleByName` method represents the name of
     * the Keycloak role that you want to retrieve. This method sends a request to the Keycloak server
     * to fetch the details of the role with the specified name.
     * @param accessToken An access token is a credential used to access protected resources on behalf
     * of a user. It is typically obtained after a user authenticates and authorizes access to their
     * data. In the context of the `getRoleByName` method you provided, the `accessToken` parameter is
     * the token that grants access to
     * @return The `getRoleByName` method returns a `Mono` object that will eventually emit a
     * `KeycloakRole` instance after making a request to the Keycloak server using the provided
     * `roleName` and `accessToken`. The method also includes logging statements for success and error
     * scenarios.
     */
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


    /**
     * This function assigns roles to a user in Keycloak using a WebClient in a reactive programming
     * style with error logging.
     * 
     * @param userId The `userId` parameter represents the unique identifier of the user in Keycloak to
     * whom you want to assign roles.
     * @param roles The `roles` parameter in the `assignRolesToUserInKeycloak` method represents a list
     * of KeycloakRole objects that you want to assign to a user in Keycloak. These roles define the
     * permissions and access levels that the user will have within the Keycloak realm.
     * @param accessToken The `accessToken` parameter in the `assignRolesToUserInKeycloak` method is a
     * string that represents the access token required for authentication and authorization when
     * making requests to the Keycloak server. This access token is typically obtained after a
     * successful authentication process and is used to access protected resources on the server
     * @return The method `assignRolesToUserInKeycloak` returns a `Mono<Void>`, which is a reactive
     * type in Project Reactor representing a completion signal without a value. This method is used to
     * assign roles to a user in Keycloak by making a POST request to the Keycloak API with the
     * provided user ID, roles, and access token.
     */
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


    /**
     * This function retrieves roles for a user in Keycloak using a WebClient and handles different
     * HTTP status codes appropriately.
     * 
     * @param userId The `userId` parameter in the `getRolesForUserInKeycloak` method represents the
     * unique identifier of the user for whom you want to retrieve roles in Keycloak. This method makes
     * a request to Keycloak's API to fetch the roles assigned to the specified user.
     * @param accessToken The `accessToken` parameter in the `getRolesForUserInKeycloak` method is
     * typically a token that is used for authentication and authorization purposes. In this case, it
     * is being used to authorize the request to Keycloak in order to retrieve the roles for a specific
     * user.
     * @return The method `getRolesForUserInKeycloak` returns a `Flux` of `KeycloakRole` objects. It
     * makes a request to Keycloak API to retrieve the role mappings for a specific user in a Keycloak
     * realm using the provided `userId` and `accessToken`. The method handles 4xx client errors and
     * 5xx server errors by logging the error details and returning a `
     */
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
                                    return Mono.error(new RuntimeException("Client error: " + errorBody));
                                }))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("500 Server Error: {} - {}", clientResponse.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("Server error: " + errorBody));
                                }))
                .bodyToFlux(KeycloakRole.class)
                .doOnError(error -> log.error("Error retrieving roles for user {}: {}", userId, error.getMessage()));
    }


    /**
     * This function removes roles from a user in Keycloak using a WebClient in a reactive way.
     * 
     * @param userId The `userId` parameter in the `removeRolesFromUserInKeycloak` method represents
     * the unique identifier of the user in Keycloak from whom you want to remove the specified roles.
     * @param roles The `roles` parameter is a list of `KeycloakRole` objects that represent the roles
     * to be removed from a user in Keycloak.
     * @param accessToken An access token used for authentication and authorization purposes.
     * @return The `removeRolesFromUserInKeycloak` method returns a `Mono<Void>`, which represents a
     * completion signal without a value.
     */
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

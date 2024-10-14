package com.medica.medicamanagement.user_service.service;

import com.medica.dto.KeycloakRole;
import com.medica.dto.UserRequest;
import com.medica.dto.UserResponse;
import com.medica.medicamanagement.user_service.client.KeyCloakRoleServiceClient;
import com.medica.medicamanagement.user_service.client.KeyCloakTokenServiceClient;
import com.medica.medicamanagement.user_service.client.KeyCloakUserServiceClient;
import com.medica.medicamanagement.user_service.dao.RoleRepository;
import com.medica.medicamanagement.user_service.dao.UserRepository;
import com.medica.medicamanagement.user_service.dao.UserRoleRepository;
import com.medica.medicamanagement.user_service.model.Role;
import com.medica.medicamanagement.user_service.model.User;
import com.medica.medicamanagement.user_service.model.UserRole;
import com.medica.medicamanagement.user_service.util.RepositoryUtility;
import com.medica.medicamanagement.user_service.util.ResponseMakerUtility;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * The `UserServiceImplementation` class in Java implements methods for creating, updating, retrieving,
 * and deleting users while interacting with Keycloak services and repositories.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final KeyCloakUserServiceClient keyCloakUserServiceClient;
    private final KeyCloakRoleServiceClient keyCloakRoleServiceClient;
    private final DatabaseClient databaseClient;
    private final KeyCloakTokenServiceClient keyCloakTokenServiceClient;

    /**
     * This function creates a user with specified roles after obtaining an admin access token and
     * handling any errors that may occur during the process.
     * 
     * @param userRequest The `userRequest` parameter in the `createUser` method is an object of type
     * `UserRequest`, which likely contains information about the user to be created. This information
     * may include details such as the user's username, email, password, and roles.
     * @return The `createUser` method returns a `Mono<UserResponse>`.
     */
    @Override
    public Mono<UserResponse> createUser(UserRequest userRequest) {
        return keyCloakTokenServiceClient.getAdminAccessToken()
                .flatMap(accessToken -> Flux.fromIterable(userRequest.getRoles())
                        .flatMap(roleReq -> keyCloakRoleServiceClient.getRoleByName(roleReq.getName(), accessToken)
                                .onErrorResume(error -> {
                                    log.error("Failed to retrieve role {}: {}", roleReq.getName(), error.getMessage());
                                    return Mono.empty();
                                }))
                        .collectList()
                        .flatMap(keycloakRoles -> {
                            List<Role> roles = mapKeycloakRolesToRoles(keycloakRoles);
                            return createUserAndAssignRoles(userRequest, keycloakRoles, accessToken)
                                    .flatMap(user -> ResponseMakerUtility.getUserCreateResponse(user, roles));
                        }))
                .doOnError(error -> log.error("Error creating user: {}", error.getMessage()));
    }



    /**
     * This function retrieves a user by their ID, fetches their roles, and creates a response object
     * with the user and roles information.
     * 
     * @param userId The `userId` parameter is a String representing the unique identifier of a user.
     * It is used to retrieve user information from the repository based on this identifier.
     * @return A `Mono<UserResponse>` is being returned.
     */
    @Override
    public Mono<UserResponse> getUserById(String userId) {
        return RepositoryUtility.getUserById(UUID.fromString(userId), userRepository)
                .flatMap(user -> getUserRoles(user.getId())
                        .collectList()
                        .flatMap(roles -> ResponseMakerUtility.getUserCreateResponse(user, roles))
                );
    }

    /**
     * This function updates a user's details and roles based on the provided user ID and request.
     * 
     * @param userId The `userId` parameter is a String representing the unique identifier of the user
     * that needs to be updated.
     * @param userRequest The `userRequest` parameter in the `updateUser` method is an object of type
     * `UserRequest`. It likely contains the updated details or information for a user that needs to be
     * applied during the update process.
     * @return The `updateUser` method returns a `Mono` of `UserResponse`.
     */
    @Override
    public Mono<UserResponse> updateUser(String userId, UserRequest userRequest) {
        return RepositoryUtility.getUserById(UUID.fromString(userId), userRepository)
                .flatMap(user -> updateUserDetails(user, userRequest))
                .flatMap(updatedUser -> handleRoleUpdates(updatedUser, userRequest)
                        .flatMap(tuple -> {
                            User user = tuple.getT1();
                            List<KeycloakRole> keycloakRoles = tuple.getT2();
                            List<Role> roles = keycloakRoles.stream()
                                    .map(keycloakRole -> Role.builder().name(keycloakRole.getName()).build())
                                    .collect(Collectors.toList());
                            return ResponseMakerUtility.getUserCreateResponse(user, roles);
                        })
                );
    }

    /**
     * This function deletes a user by their ID, first retrieving the user from a repository, deleting
     * the user from the repository, logging any errors, and then deleting the user from Keycloak.
     * 
     * @param userId The `userId` parameter is a String representing the unique identifier of the user
     * to be deleted.
     * @return The `deleteUser` method returns a `Mono<Void>` which represents a completion signal
     * without a value.
     */
    @Override
    public Mono<Void> deleteUser(String userId) {
        return RepositoryUtility.getUserById(UUID.fromString(userId), userRepository)
                .flatMap(user -> userRepository.delete(user)
                        .doOnError(error -> log.error("Error deleting user: {}", error.getMessage()))
                        .then(keyCloakUserServiceClient.deleteUserInKeycloak(user.getIdentityProviderUserId())));
    }

    /**
     * This function creates a user in Keycloak, assigns roles to the user, saves the user in a
     * repository, and saves the user roles.
     * 
     * @param userRequest The `userRequest` parameter is an object that contains the information needed
     * to create a new user. This information typically includes the user's first name, last name,
     * username, email, phone number, address, age, etc.
     * @param keycloakRoles A list of Keycloak roles that you want to assign to the user being created.
     * @param accessToken The `accessToken` parameter in the `createUserAndAssignRoles` method is used
     * to authenticate the request to Keycloak when assigning roles to the user in Keycloak. This
     * access token is typically obtained during the authentication process and is used to authorize
     * the API calls to Keycloak on behalf of the user
     * @return The `createUserAndAssignRoles` method returns a `Mono<User>` object, which represents a
     * reactive stream emitting a single `User` object.
     */
    private Mono<User> createUserAndAssignRoles(UserRequest userRequest, List<KeycloakRole> keycloakRoles, String accessToken) {
        User user = createUserEntity(userRequest);

        return keyCloakUserServiceClient.createUserInKeycloak(userRequest)
                .flatMap(keycloakUserId -> keyCloakRoleServiceClient.assignRolesToUserInKeycloak(keycloakUserId, keycloakRoles, accessToken)
                        .thenReturn(keycloakUserId))
                .flatMap(keycloakUserId -> {
                    user.setIdentityProviderUserId(keycloakUserId);
                    user.setId(UUID.fromString(DefaultValuesPopulator.getUid()));

                    return userRepository.insert(
                                    user.getId(),
                                    user.getFirstName(),
                                    user.getLastName(),
                                    user.getUsername(),
                                    user.getEmail(),
                                    user.getPhone(),
                                    user.getAddress(),
                                    user.getAge(),
                                    user.getIdentityProviderUserId(),
                                    user.getCreatedAt(),
                                    user.getUpdatedAt()
                            )
                            .flatMap(savedUser -> saveUserRoles(savedUser, mapKeycloakRolesToRoles(keycloakRoles)).thenReturn(savedUser));
                });
    }


    /**
     * The function `createUserEntity` creates a User entity object using data from a UserRequest
     * object.
     * 
     * @param userRequest The `userRequest` parameter is an object of type `UserRequest` which contains
     * the following properties:
     * @return An entity of type User is being returned, which is created based on the data provided in
     * the UserRequest object.
     */
    private User createUserEntity(UserRequest userRequest) {
        return User.builder().firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName()).age(userRequest.getAge()).email(userRequest.getEmail())
                .username(userRequest.getUsername()).phone(userRequest.getPhone()).address(userRequest.getAddress())
                .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                .build();
    }

    /**
     * The function `mapKeycloakRolesToRoles` maps a list of KeycloakRole objects to a list of Role
     * objects.
     * 
     * @param keycloakRoles A list of KeycloakRole objects that need to be mapped to Role objects. The
     * method `mapKeycloakRolesToRoles` takes this list of KeycloakRole objects and maps them to Role
     * objects by extracting the id, name, and description attributes from each KeycloakRole object.
     * The method then
     * @return The method `mapKeycloakRolesToRoles` returns a `List<Role>` after mapping the
     * `KeycloakRole` objects to `Role` objects.
     */
    private List<Role> mapKeycloakRolesToRoles(List<KeycloakRole> keycloakRoles) {
        return keycloakRoles.stream()
                .map(keycloakRole -> Role.builder()
                        .id(keycloakRole.getId())
                        .name(keycloakRole.getName())
                        .description(keycloakRole.getDescription())
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * This function saves user roles by mapping role names to role IDs and then inserting user roles
     * into the database.
     * 
     * @param savedUser The `savedUser` parameter in the `saveUserRoles` method represents the user
     * entity that has been saved and for which roles need to be assigned. This user entity typically
     * contains information such as the user's ID, name, email, and other relevant details.
     * @param roles List of roles to assign to the user
     * @return The `saveUserRoles` method returns a `Mono<Void>` which represents a completion signal
     * without a value.
     */
    private Mono<Void> saveUserRoles(User savedUser, List<Role> roles) {
        List<String> roleNames = roles.stream().map(Role::getName).collect(Collectors.toList());

        return roleRepository.findByNameIn(roleNames).collectList().flatMap(roleEntities -> {
                    Map<String, String> roleIdMap = roleEntities.stream().collect(Collectors.toMap(Role::getName, Role::getId));

                    List<UserRole> userRoles = roles.stream().map(role -> {
                                String roleId = roleIdMap.get(role.getName());
                                if (roleId != null) {
                                    return UserRole.builder()
                                            .id(UUID.fromString(DefaultValuesPopulator.getUid())).userId(savedUser.getId()).roleId(roleId)
                                            .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                                            .build();
                                } else {
                                    log.warn("Role {} not found in the role table", role.getName());
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    return RepositoryUtility.batchInsertUserRoles(userRoles, databaseClient);
                });
    }

    /**
     * The function `getUserRoles` retrieves roles associated with a user by querying
     * userRoleRepository and roleRepository.
     * 
     * @param userId The `userId` parameter is a unique identifier for a user, typically represented as
     * a `UUID` (Universally Unique Identifier). It is used to retrieve the roles associated with a
     * specific user in the `getUserRoles` method.
     * @return A Flux of Role objects is being returned. The method retrieves user roles from the
     * userRoleRepository based on the userId, then retrieves the corresponding Role objects from the
     * roleRepository using the roleId obtained from the userRole.
     */
    private Flux<Role> getUserRoles(UUID userId) {
        return userRoleRepository.findByUserId(userId)
                .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()));
    }

    /**
     * The function `handleRoleUpdates` updates user roles in Keycloak based on the roles provided in a
     * user request.
     * 
     * @param updatedUser The `updatedUser` parameter is an object representing a user whose
     * information is being updated. It likely contains details such as the user's identity provider
     * user ID, username, email, and other relevant information. In the provided code snippet, this
     * user object is used to fetch current roles, assign new roles
     * @param userRequest The `userRequest` parameter in the `handleRoleUpdates` method represents a
     * request object containing information about a user's roles. This object is used to determine the
     * new roles that should be assigned to a user in Keycloak. The method processes this request to
     * update the user's roles in Keycloak
     * @return The `handleRoleUpdates` method returns a `Mono` that emits a `Tuple2` containing the
     * updated `User` object and a list of `KeycloakRole` objects.
     */
    private Mono<Tuple2<User, List<KeycloakRole>>> handleRoleUpdates(User updatedUser, UserRequest userRequest) {
        return keyCloakTokenServiceClient.getAdminAccessToken()
                .flatMap(accessToken -> {
                    // Fetch current roles
                    Flux<KeycloakRole> currentRoles = keyCloakRoleServiceClient.getRolesForUserInKeycloak(updatedUser.getIdentityProviderUserId(), accessToken);

                    // Fetch new roles from request
                    Flux<KeycloakRole> newKeycloakRoles = Flux.fromIterable(userRequest.getRoles())
                            .flatMap(roleReq -> keyCloakRoleServiceClient.getRoleByName(roleReq.getName(), accessToken));

                    return currentRoles.collectList().zipWith(newKeycloakRoles.collectList(), (currentList, newList) -> {
                        // Determine roles to add
                        List<KeycloakRole> rolesToAdd = newList.stream()
                                .filter(role -> !currentList.contains(role))
                                .collect(Collectors.toList());

                        // Assign new roles
                        if (!rolesToAdd.isEmpty()) {
                            keyCloakRoleServiceClient.assignRolesToUserInKeycloak(updatedUser.getIdentityProviderUserId(), rolesToAdd, accessToken)
                                    .doOnSuccess(aVoid -> log.info("New user roles added successfully."))
                                    .doOnError(error -> log.error("Error adding user roles: {}", error.getMessage()))
                                    .subscribe();
                        }

                        // Update user in Keycloak
                        this.keyCloakUserServiceClient.updateUserInKeycloak(updatedUser.getIdentityProviderUserId(), userRequest, accessToken)
                                .doOnSuccess(userId -> log.info("User updated: {}", userId))
                                .doOnError(error -> log.error("Error updating user: {}", error.getMessage()))
                                .subscribe();

                        return Tuples.of(updatedUser, newList);
                    });
                });
    }

    /**
     * This function updates the details of a user with the information provided in a user request
     * object and saves the updated user in the repository.
     * 
     * @param user The `user` parameter is an instance of the `User` class, which likely represents a
     * user entity in your application. It contains details such as first name, last name, address,
     * phone, email, age, and updated timestamp.
     * @param userRequest UserRequest object containing the updated user details
     * @return The method `updateUserDetails` is returning a `Mono<User>` object after updating the
     * user details with the values provided in the `UserRequest` object.
     */
    private Mono<User> updateUserDetails(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setAddress(userRequest.getAddress());
        user.setPhone(userRequest.getPhone());
        user.setEmail(userRequest.getEmail());
        user.setAge(userRequest.getAge());
        user.setUpdatedAt(DefaultValuesPopulator.getCurrentTimestamp());

        return userRepository.save(user).doOnError(error -> log.error("Error updating user {}", error.getMessage()));
    }
}

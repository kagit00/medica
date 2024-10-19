package com.medica.medicamanagement.user_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.medica.dto.*;
import com.medica.exception.InternalServerErrorException;
import com.medica.medicamanagement.user_service.client.KeyCloakCredentialServiceClient;
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
import com.medica.medicamanagement.user_service.util.SecurityUtility;
import com.medica.util.BasicUtility;
import com.medica.util.Constant;
import com.medica.util.DefaultValuesPopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final KeyCloakUserServiceClient keyCloakUserServiceClient;
    private final KeyCloakRoleServiceClient keyCloakRoleServiceClient;
    private final DatabaseClient databaseClient;
    private final KeyCloakTokenServiceClient keyCloakTokenServiceClient;
    private final KeyCloakCredentialServiceClient keyCloakCredentialServiceClient;
    private final BCryptPasswordEncoder encoder;
    private final PubSubTemplate pubSubTemplate;

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


    @Override
    public Mono<UserResponse> getUserById(String userId) {
        return RepositoryUtility.getUserById(UUID.fromString(userId), userRepository)
                .flatMap(user -> getUserRoles(user.getId())
                        .collectList()
                        .flatMap(roles -> ResponseMakerUtility.getUserCreateResponse(user, roles))
                );
    }

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


    @Override
    public Mono<Void> deleteUser(String userId) {
        return RepositoryUtility.getUserById(UUID.fromString(userId), userRepository)
                .flatMap(user -> userRepository.delete(user)
                        .doOnError(error -> log.error("Error deleting user: {}", error.getMessage()))
                        .then(keyCloakUserServiceClient.deleteUserInKeycloak(user.getIdentityProviderUserId())));
    }

    @Override
    public Mono<NotificationResponse> changePassword(String username, UserPasswordRequest userPasswordRequest) {
        return this.userRepository.findByUserName(username)
                .flatMap($user -> {
                    // Check if the new password matches the current password
                    if (!encoder.matches(userPasswordRequest.getCurrentPassword(), $user.getPassword())) {
                        return ResponseMakerUtility.getNotificationResponse("Wrong Current Password", HttpStatus.BAD_REQUEST.name());
                    }

                    // Check if the confirmed password matches the new password
                    if (!userPasswordRequest.getNewPassword().equals(userPasswordRequest.getConfirmedPassword())) {
                        return ResponseMakerUtility.getNotificationResponse("confirmed password doesn't match the new password", HttpStatus.BAD_REQUEST.name());
                    }

                    // Publish password change event
                    pubSubTemplate.publish(
                            "user-password-changed",
                            $user.getEmail() + " <> " + $user.getFirstName()
                    );

                    // Set new password
                    $user.setPassword(encoder.encode(userPasswordRequest.getNewPassword()));

                    // Update password in Keycloak and save user
                    return Mono.fromRunnable(() ->
                                    this.keyCloakCredentialServiceClient.setUserPassword(
                                            $user.getIdentityProviderUserId(),
                                            userPasswordRequest.getNewPassword(),
                                            userPasswordRequest.getCurrentPassword()
                                    )
                            )
                            .then(
                                    this.userRepository.save($user)
                                            .flatMap(updatedUser -> {
                                                log.info("Password got changed for the user {}", username);
                                                return ResponseMakerUtility.getNotificationResponse("Password Info Mailed To you", HttpStatus.OK.name());
                                            })
                            )
                            .onErrorResume(e -> ResponseMakerUtility.getNotificationResponse(
                                    "Error While Updating User " + e.getMessage(),
                                    HttpStatus.INTERNAL_SERVER_ERROR.name()
                            ));
                })
                .onErrorResume(e -> ResponseMakerUtility.getNotificationResponse(
                        "Error While Fetching User Info " + e.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR.name()
                ));
    }


    private Mono<User> createUserAndAssignRoles(UserRequest userRequest, List<KeycloakRole> keycloakRoles, String accessToken) {
        User user = createUserEntity(userRequest);

        return keyCloakUserServiceClient.createUserInKeycloak(userRequest)
                .flatMap(keycloakUserId -> keyCloakRoleServiceClient.assignRolesToUserInKeycloak(keycloakUserId, keycloakRoles, accessToken)
                        .thenReturn(keycloakUserId))
                .flatMap(keycloakUserId -> {
                    user.setIdentityProviderUserId(keycloakUserId);
                    user.setId(UUID.fromString(DefaultValuesPopulator.getUid()));
                    user.setPassword(SecurityUtility.getComplexPassword());

                    return Mono.fromRunnable(() -> this.keyCloakCredentialServiceClient.setUserPassword(keycloakUserId, user.getPassword(), null))
                            .then(
                                    userRepository.insert(
                                                    user.getId(),
                                                    user.getFirstName(),
                                                    user.getLastName(),
                                                    user.getUsername(),
                                                    encoder.encode(user.getPassword()),
                                                    user.getEmail(),
                                                    user.getPhone(),
                                                    user.getAddress(),
                                                    user.getAge(),
                                                    user.getIdentityProviderUserId(),
                                                    user.getCreatedAt(),
                                                    user.getUpdatedAt()
                                            )
                                            .flatMap(savedUser -> saveUserRoles(
                                                    savedUser,
                                                    mapKeycloakRolesToRoles(keycloakRoles)).thenReturn(user)
                                            )
                            );
                });
    }


    private User createUserEntity(UserRequest userRequest) {
        return User.builder().firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName()).age(userRequest.getAge()).email(userRequest.getEmail())
                .username(userRequest.getUsername()).phone(userRequest.getPhone()).address(userRequest.getAddress())
                .createdAt(DefaultValuesPopulator.getCurrentTimestamp()).updatedAt(DefaultValuesPopulator.getCurrentTimestamp())
                .build();
    }


    private List<Role> mapKeycloakRolesToRoles(List<KeycloakRole> keycloakRoles) {
        return keycloakRoles.stream()
                .map(keycloakRole -> Role.builder()
                        .id(keycloakRole.getId())
                        .name(keycloakRole.getName())
                        .description(keycloakRole.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

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

    private Flux<Role> getUserRoles(UUID userId) {
        return userRoleRepository.findByUserId(userId)
                .flatMap(userRole -> roleRepository.findById(userRole.getRoleId()));
    }

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

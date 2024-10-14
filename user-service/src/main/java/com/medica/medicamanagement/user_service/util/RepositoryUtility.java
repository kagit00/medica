package com.medica.medicamanagement.user_service.util;

import com.medica.exception.BadRequestException;
import com.medica.medicamanagement.user_service.dao.UserRepository;
import com.medica.medicamanagement.user_service.model.User;
import com.medica.medicamanagement.user_service.model.UserRole;
import com.medica.util.DefaultValuesPopulator;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * The `RepositoryUtility` class provides static methods for retrieving a user by ID from a repository
 * and batch inserting user roles into a database.
 */
public final class RepositoryUtility {

    private RepositoryUtility() {
        throw new UnsupportedOperationException("Unsupported Operation");
    }

    /**
     * Gets user by id.
     *
     * @param userId         the user id
     * @param userRepository the user repository
     * @return the user by id
     */
    public static Mono<User> getUserById(UUID userId, UserRepository userRepository) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new BadRequestException("User Not Found By Id: " + userId)));
    }

    /**
     * Batch insert user roles mono.
     *
     * @param userRoles      the user roles
     * @param databaseClient the database client
     * @return the mono
     */
    public static Mono<Void> batchInsertUserRoles(List<UserRole> userRoles, DatabaseClient databaseClient) {
        return Flux.fromIterable(userRoles)
                .flatMap(userRole -> databaseClient.sql("INSERT INTO user_roles (id, user_id, role_id, created_at, updated_at) " +
                                "VALUES (:id, :userId, :roleId, :createdAt, :updatedAt)")
                        .bind("id", UUID.fromString(DefaultValuesPopulator.getUid()))
                        .bind("userId", userRole.getUserId())
                        .bind("roleId", userRole.getRoleId())
                        .bind("createdAt", userRole.getCreatedAt())
                        .bind("updatedAt", userRole.getUpdatedAt())
                        .then())
                .then();
    }

}

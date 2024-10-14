package com.medica.medicamanagement.user_service.dao;

import com.medica.medicamanagement.user_service.model.UserRole;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import java.util.UUID;

// This code snippet is defining a repository interface in a Java Spring application.
@Repository
public interface UserRoleRepository extends R2dbcRepository<UserRole, String> {
    /**
     * The function `findByUserId` returns a Flux of UserRole objects based on the provided userId.
     * 
     * @param userId The `userId` parameter is a unique identifier for a user in the system. It is of
     * type `UUID`, which stands for Universally Unique Identifier. This identifier is used to uniquely
     * identify a specific user in the system.
     * @return This method is returning a Flux of UserRole objects based on the provided userId. The
     * Flux class in Spring represents a stream of zero or more UserRole objects, allowing for
     * asynchronous processing of the results.
     */
    Flux<UserRole> findByUserId(UUID userId);
}
